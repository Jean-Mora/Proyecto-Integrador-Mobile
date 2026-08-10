# Registro de Decisión Arquitectónica (ADR)

## Datos Informativos

- **ID:** ADR-0003
- **Título:** DTOs en español mapeados al contrato en inglés del backend vía `@SerializedName`
- **Fecha:** 09/08/2026
- **Autores:** Jean Pierre Mora Santillán, Luis Mateo Rivera Escalante
- **Estado Actual:** Aceptado

## 1. Estado

Aceptado tras detectar, en una iteración intermedia del proyecto, que
todas las peticiones a la API devolvían error 500: el backend real
expone rutas y campos JSON en inglés (`/equipment`, `categoryName`,
`AVAILABLE`) mientras la app ya tenía construida una capa de UI completa
que asumía nombres en español (`/equipos`, `categoriaNombre`,
`DISPONIBLE`). Vigente desde entonces en los cuatro DTOs de dominio
(`EquipoDtos`, `CategoriaDtos`, `PrestamoDtos`, `IncidenciaDtos`).

## 2. Contexto

**Problema:** había que resolver el desajuste de idioma/convención entre
el contrato real del backend (en inglés) y el resto de la app (en
español: nombres de clases, layouts, `strings.xml`, lógica de UI), sin
poder modificar el backend a discreción del equipo móvil.

**Requerimientos asociados:** RNF-08 del `SRS.md` (los DTOs deben mapear
el contrato del backend sin requerir cambios en la capa de presentación
cuando el backend renombra un campo JSON).

**Factores influyentes:** en el momento del descubrimiento, la app ya
tenía implementadas varias pantallas (catálogo, detalle de equipo,
solicitar préstamo) con toda su UI, sus `strings.xml` y su lógica de
`ViewModel` escrita asumiendo nombres en español; reescribir esa capa
completa para usar inglés habría significado renombrar decenas de
identificadores en Kotlin y XML con alto riesgo de introducir errores,
por un cambio que no aporta valor funcional.

## 3. Decisión

**Descripción:** cada DTO mantiene sus propiedades y sus valores de
`enum` en español (los mismos nombres ya usados en toda la UI), y cada
propiedad/constante de `enum` se anota individualmente con
`@SerializedName("nombreEnIngles")` de Gson, de forma que la
serialización/deserialización JSON traduce automáticamente en ambas
direcciones sin que ningún otro archivo del proyecto necesite saber que
existe una diferencia de idioma. Por ejemplo:

```kotlin
enum class EstadoEquipo {
    @SerializedName("AVAILABLE") DISPONIBLE,
    @SerializedName("LOANED") PRESTADO,
    @SerializedName("MAINTENANCE") MANTENIMIENTO
}

data class EquipoResponse(
    val id: Long,
    @SerializedName("categoryName") val categoriaNombre: String,
    @SerializedName("status") val estado: EstadoEquipo,
    // ...
)
```

Las rutas de `ApiService` (`@GET("equipment")`, `@GET("categories")`,
etc.) también se corrigieron para usar los nombres en inglés reales del
backend, ya que Retrofit no tiene un mecanismo de mapeo equivalente para
rutas — esa parte del contrato sí se actualizó literalmente.

**Alcance:** afecta únicamente `data/remote/dto/*.kt` y las rutas
declaradas en `data/remote/ApiService.kt`; ningún `Fragment`, `ViewModel`,
`Adapter`, layout XML ni recurso de `strings.xml` cambió como
consecuencia de esta decisión.

**Justificación técnica:** aislar el mapeo de nombres en la capa de
transporte (DTOs) es la aplicación directa del principio de una única
responsabilidad por capa: la capa de red es la única que debe conocer el
formato exacto del contrato HTTP; el resto de la app trabaja con el
vocabulario del dominio en español ya establecido, reduciendo el
acoplamiento entre el contrato externo (que el equipo móvil no controla)
y la capa de presentación (que sí controla y que ya tenía una inversión
considerable de trabajo hecho).

## 4. Consecuencias (Trade-offs)

**Resultados Positivos (Garantías):**
- Cero cambios en la capa de presentación para corregir el desajuste de
  contrato: la corrección completa fue posible tocando solo cinco
  archivos (`ApiService.kt` + los cuatro DTOs).
- Si el backend cambiara el nombre de un campo JSON en el futuro (sin
  cambiar su significado), la corrección quedaría igualmente aislada a
  una anotación `@SerializedName`, verificado en esta misma iteración al
  agregar `GET /incidents` sin tocar ningún archivo de UI.
- El vocabulario de dominio en español permanece consistente en toda la
  app (Kotlin, XML, `strings.xml`), que es el idioma en el que está
  escrita la especificación original del proyecto
  (`sigpel_pantallas_moviles.md`).

**Resultados Negativos (Pasivos/Deuda):**
- Cada DTO requiere mantenimiento manual y cuidadoso: si se agrega una
  propiedad nueva al backend y se olvida la anotación
  `@SerializedName` correspondiente (o se anota mal), Gson usará el
  nombre en español tal cual como clave JSON, y el campo llegará
  silenciosamente `null` sin que Retrofit reporte un error explícito —
  un tipo de bug detectado únicamente inspeccionando manualmente el
  cuerpo de la respuesta en `logcat` durante el desarrollo.
- Un desarrollador nuevo en el proyecto debe conocer ambos vocabularios
  (el de dominio en español y el contrato real en inglés) para depurar
  cualquier problema de serialización, en vez de que el código sea
  autoexplicativo con un solo idioma de principio a fin.
