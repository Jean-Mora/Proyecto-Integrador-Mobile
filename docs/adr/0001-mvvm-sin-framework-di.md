# Registro de Decisión Arquitectónica (ADR)

## Datos Informativos

- **ID:** ADR-0001
- **Título:** MVVM con inyección de dependencias manual, sin Hilt/Koin
- **Fecha:** 09/08/2026
- **Autores:** Jean Pierre Mora Santillán, Luis Mateo Rivera Escalante
- **Estado Actual:** Aceptado

## 1. Estado

Aceptado desde el inicio del proyecto (HU-18, primera pantalla con
sesión) y vigente en la versión actual, con 11 pantallas y 5
repositorios construidos bajo el mismo patrón.

## 2. Contexto

**Problema:** cada pantalla necesita acceso a uno o más `Repository`
(`EquipoRepository`, `PrestamoRepository`, `CategoriaRepository`,
`IncidenciaRepository`, `AuthRepository`), y cada `Repository` necesita
`ApiService` y, en el caso de `AuthRepository`, también
`SessionManager`. Había que decidir cómo se construyen y comparten esas
instancias entre las 11 pantallas sin duplicar código de inicialización
en cada una.

**Requerimientos asociados:** RF-005 (SRS.md, navegación por rol, que
depende de `AuthRepository` en `MainActivity`), y de forma transversal
todos los RF de este SRS, ya que cada pantalla con datos depende de al
menos un `Repository`.

**Factores influyentes:** equipo de dos personas, cronograma académico
fijo, y necesidad de aprender simultáneamente Android, el contrato del
backend y el flujo de Cognito — agregar un framework de inyección de
dependencias (Hilt o Koin) habría sumado una curva de aprendizaje
adicional (anotaciones `@HiltViewModel`, módulos `@Provides`, o el DSL de
Koin) sin un beneficio claro dado el tamaño real del grafo de
dependencias.

## 3. Decisión

**Descripción:** `SigpelApp` (la clase `Application`) actúa como
contenedor manual de dependencias: en `onCreate()` construye, en orden,
`SessionManager`, `ApiService` (vía `NetworkModule`), `CognitoAuthApi`, y
los cinco `Repository`, exponiéndolos como propiedades públicas
(`lateinit var ... private set`). Cada `Fragment` obtiene su
`ViewModel` con una función auxiliar mínima,
`simpleViewModelFactory { ViewModelXyz(sigpelApp.repositorioA, sigpelApp.repositorioB) }`,
que envuelve un `ViewModelProvider.Factory` de una sola línea.

**Alcance:** afecta `SigpelApp.kt`, la extensión `Fragment.sigpelApp`
(en `ui/common/ViewExt.kt`), y la línea `by viewModels { simpleViewModelFactory {...} }`
al inicio de cada `Fragment` que necesita un `ViewModel`.

**Justificación técnica:** el grafo de dependencias real del proyecto es
plano — ningún `Repository` depende de otro `Repository`, y ningún
`ViewModel` tiene más de dos dependencias directas. Un contenedor manual
resuelve ese grafo con el mismo resultado funcional que un framework de
DI, pero sin *code generation*, sin anotaciones, y con el costo de
inicialización visible en un único lugar (`SigpelApp.onCreate()`),
facilitando razonar sobre el orden de construcción sin depender de la
"magia" de un framework (principio de simplicidad frente a
sobre-ingeniería, alineado con maximizar cohesión sin introducir
acoplamiento innecesario a una librería externa — Senn, 2004).

## 4. Consecuencias (Trade-offs)

**Resultados Positivos (Garantías):**
- Cero dependencias de compilación adicionales (sin `kapt`/KSP para
  Hilt, sin el DSL de Koin), lo que mantiene los tiempos de compilación
  bajos y el `build.gradle.kts` legible para un equipo que recién
  aprende Android.
- El orden y el costo de construcción de cada dependencia es explícito y
  rastreable línea por línea en `SigpelApp.onCreate()`, sin necesidad de
  entender el ciclo de vida de un *framework* de DI para depurar un
  problema de inicialización.
- Patrón uniforme y fácil de replicar: agregar una pantalla nueva (como
  ocurrió con `GestionEquiposFragment` y `GestionIncidenciasFragment` en
  esta iteración) no requiere tocar ningún módulo de configuración de DI,
  solo repetir la misma línea de `simpleViewModelFactory`.

**Resultados Negativos (Pasivos/Deuda):**
- No escala bien si el número de dependencias creciera
  significativamente: cada `ViewModelFactory` se repite manualmente en
  cada `Fragment` en vez de generarse automáticamente, y `SigpelApp` se
  volvería un archivo cada vez más largo.
- Sin *scoping* automático (por ejemplo, dependencias con ciclo de vida
  atado a un `NavGraph` o a una pantalla específica) — todo lo que vive
  en `SigpelApp` es efectivamente un *singleton* de toda la app, lo cual
  es correcto para este proyecto pero sería una limitación real en uno
  más grande.
- Sin soporte nativo para *testing* con dependencias falsas
  intercambiables vía un grafo de DI (p. ej. un módulo de test de Hilt);
  cualquier prueba unitaria futura tendría que instanciar los
  `Repository`/`ViewModel` manualmente con dobles de prueba, lo cual es
  factible pero menos ergonómico que con un framework de DI (relacionado
  con la deuda técnica de ausencia de pruebas automatizadas, ver
  `SAD.md` §9).
