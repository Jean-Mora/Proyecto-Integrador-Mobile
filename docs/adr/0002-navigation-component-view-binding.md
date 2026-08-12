# Registro de Decisión Arquitectónica (ADR)

## Datos Informativos

- **ID:** ADR-0002
- **Título:** Single Activity + Navigation Component + View Binding, sin Jetpack Compose
- **Fecha:** 09/08/2026
- **Autores:** Jean Pierre Mora Santillán, Luis Mateo Rivera Escalante
- **Estado Actual:** Aceptado

## 1. Estado

Aceptado desde el diseño inicial de pantallas
(`sigpel_pantallas_moviles.md`) y vigente en las 11 pantallas actuales,
todas alojadas en el mismo `NavHostFragment` de `MainActivity`.

## 2. Contexto

**Problema:** había que elegir el enfoque de construcción de UI (Views
clásicas vs. Jetpack Compose) y el mecanismo de navegación entre
pantallas (múltiples `Activity` vs. una sola `Activity` con
`Fragment`s) antes de empezar a construir cualquier pantalla.

**Requerimientos asociados:** RF-005 del `SRS.md` (navegación por rol
consistente en toda la app) y RNF-06 (adaptabilidad de la navegación
principal entre teléfono y tablet).

**Factores influyentes:** equipo con más experiencia previa en el
sistema de Views clásico de Android que en Compose; cronograma académico
fijo que no permitía absorber la curva de aprendizaje de un paradigma
declarativo nuevo al mismo tiempo que se aprendía el resto del stack
(Retrofit, Cognito, Navigation Component); y once pantallas con
formularios, listas y diálogos — un caso de uso bien cubierto por Material
Components clásico sin necesitar las ventajas específicas de Compose
(animaciones complejas, UI altamente dinámica).

## 3. Decisión

**Descripción:** la app usa una única `MainActivity` que aloja un
`NavHostFragment` con un solo `nav_graph.xml`; cada pantalla es un
`Fragment` independiente con su propio archivo de layout XML inflado vía
View Binding (`FragmentXyzBinding.inflate(...)`), nunca `findViewById`
manual. La navegación entre pantallas usa exclusivamente
`findNavController().navigate(...)`, con acciones (`<action>`) o
navegación directa por id de destino cuando no hay una acción declarada
explícitamente (patrón usado, por ejemplo, para ir de "Registrar equipo"
a "Categorías"). El menú inferior (`BottomNavigationView`) se conecta al
mismo `NavController` con `NavigationUI.setupWithNavController(...)`.

**Alcance:** afecta la totalidad del paquete `ui/*`, `nav_graph.xml`,
`activity_main.xml` (y su variante `layout-sw600dp/activity_main.xml`
con `NavigationRailView`), y la configuración de View Binding en
`build.gradle.kts` (`buildFeatures { viewBinding = true }`).

**Justificación técnica:** una sola `Activity` con Navigation Component
centraliza el control de la barra de navegación y el manejo del botón
"Atrás" del sistema en un único lugar (`MainActivity`), en vez de
duplicar esa lógica en múltiples `Activity`s. View Binding da acceso
seguro en tipos a las vistas de cada layout sin el costo en tiempo de
compilación ni la complejidad conceptual de introducir Compose junto con
el resto de la arquitectura nueva del proyecto; reduce el acoplamiento
entre la lógica de cada `Fragment` y los detalles internos de su XML
(cada `Fragment` solo conoce su propio `binding`, no un árbol de vistas
global).

## 4. Consecuencias (Trade-offs)

**Resultados Positivos (Garantías):**
- Navegación uniforme y centralizada: el manejo del rol (qué pestañas
  mostrar), del botón "Atrás" y de la barra de herramientas superior vive
  en un solo lugar (`MainActivity.refreshRoleUi()`), en vez de repetirse
  en cada `Activity`.
- Acceso a vistas sin *casts* inseguros ni `findViewById` propenso a
  errores en tiempo de ejecución (un id mal escrito falla en tiempo de
  compilación, no al tocar un botón).
- Curva de aprendizaje más baja para el equipo, permitiendo enfocar el
  tiempo disponible en la integración con el backend y Cognito en vez de
  en aprender un paradigma de UI nuevo.

**Resultados Negativos (Pasivos/Deuda):**
- Sin las ventajas de Compose para estados de UI altamente dinámicos
  (por ejemplo, animaciones de lista más elaboradas que las ya usadas
  `layout_animation_fall_down`, o *previews* de UI sin ejecutar la app),
  que habrían simplificado algo de código repetitivo de `RecyclerView.Adapter`
  + `DiffUtil.ItemCallback` presente en las 6 listas de la app.
- Cada `Fragment` debe liberar su `_binding` explícitamente en
  `onDestroyView()` (`_binding = null`) para evitar fugas de memoria — un
  detalle manual que Compose no requiere y que, de omitirse por error en
  una pantalla nueva, no se detecta en tiempo de compilación.
- La automatización de pruebas de UI mediante coordenadas de pantalla
  (usada durante el desarrollo, no parte del producto) resultó frágil
  ante *pop-ups* del sistema (el Photo Picker, por ejemplo) — un problema
  independiente de Views vs. Compose, pero que refuerza la deuda técnica
  de no tener pruebas instrumentadas con Espresso (ver `SAD.md` §9).
