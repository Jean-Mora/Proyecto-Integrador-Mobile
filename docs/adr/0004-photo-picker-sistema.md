# Registro de Decisión Arquitectónica (ADR)

## Datos Informativos

- **ID:** ADR-0004
- **Título:** Photo Picker del sistema para seleccionar la imagen de un equipo
- **Fecha:** 09/08/2026
- **Autores:** Jean Pierre Mora Santillán, Luis Mateo Rivera Escalante
- **Estado Actual:** Aceptado

## 1. Estado

Aceptado desde la implementación de "Registrar equipo con imagen" y
vigente en la versión actual; es el único punto de la app donde el
usuario selecciona un archivo del dispositivo.

## 2. Contexto

**Problema:** el backend acepta una imagen opcional al registrar un
equipo (`POST /equipment/{id}/image`, `multipart/form-data`); había que
decidir cómo la app deja que el Encargado elija esa imagen desde su
dispositivo.

**Requerimientos asociados:** RF-019 del `SRS.md` (adjuntar imagen al
registrar un equipo, usando el selector de imágenes del sistema).

**Factores influyentes:** las alternativas clásicas de Android para
elegir una imagen —`Intent.ACTION_GET_CONTENT` o solicitar el permiso de
almacenamiento/`READ_MEDIA_IMAGES`— requieren declarar un permiso en el
`AndroidManifest`, manejar el flujo de solicitud de permiso en tiempo de
ejecución (incluyendo el caso de rechazo permanente, "no volver a
preguntar"), y ese comportamiento varía entre versiones de Android (el
permiso cambió de nombre y granularidad en Android 13). El proyecto
tiene un `minSdk` 24 y `targetSdk` 34, un rango amplio donde ese manejo
manual habría sido una fuente considerable de casos borde.

## 3. Decisión

**Descripción:** la app usa el **Photo Picker** del sistema operativo
(`ActivityResultContracts.PickVisualMedia`, parte de Android Jetpack),
registrado en `RegistrarEquipoFragment` con
`registerForActivityResult(ActivityResultContracts.PickVisualMedia())`.
Al tocar el recuadro de imagen o el texto "Agregar imagen (opcional)", se
lanza `pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))`.
El `Uri` resultante se lee con `ContentResolver.openInputStream(uri)`, se
convierte a bytes y se empaqueta como `MultipartBody.Part` justo antes de
enviarse en `POST /equipment/{id}/image`.

**Alcance:** afecta únicamente `RegistrarEquipoFragment.kt` (el registro
del *launcher*, el manejador del resultado, y la función
`imagenAMultipart()`); no requiere ninguna entrada en
`AndroidManifest.xml`.

**Justificación técnica:** el Photo Picker es una interfaz del sistema
(respaldada por Google Play services / el propio sistema operativo en
Android 13+, con *backport* automático en versiones anteriores vía la
librería) que **no requiere declarar ni solicitar ningún permiso de
almacenamiento o galería**, porque el propio sistema operativo actúa como
intermediario de confianza: la app nunca obtiene acceso general a la
galería del usuario, solo al archivo puntual que este eligió. Esto
elimina por completo la superficie de código dedicada a solicitar,
verificar y manejar el rechazo de permisos en tiempo de ejecución,
maximizando cohesión (una sola API cubre el caso de uso completo) y
minimizando acoplamiento con la versión de Android del dispositivo.

## 4. Consecuencias (Trade-offs)

**Resultados Positivos (Garantías):**
- Cero permisos declarados en `AndroidManifest.xml` para esta
  funcionalidad, y cero código de manejo de permisos en tiempo de
  ejecución (solicitud, justificación, rechazo permanente).
- Comportamiento uniforme entre Android 7 (`minSdk` 24) y Android 14
  (`targetSdk` 34) sin *forks* de código por versión, confirmado
  visualmente en el emulador: el picker mostró correctamente la
  atribución a la app y las imágenes reales del dispositivo.
- Al no requerir permisos, no hay diálogo de sistema adicional que el
  usuario deba aceptar antes de poder elegir una imagen, reduciendo la
  fricción del flujo de "Registrar equipo".

**Resultados Negativos (Pasivos/Deuda):**
- Solo permite elegir **una** imagen ya existente en el dispositivo; no
  ofrece una opción integrada para tomar una foto nueva con la cámara en
  el mismo flujo (quedaría como una mejora futura: un botón "Tomar foto"
  adicional con `ActivityResultContracts.TakePicture()`).
- Durante el desarrollo se identificó que la interfaz del Photo Picker
  (una superficie de UI de Google, no de la app) fue difícil de
  automatizar con las herramientas de prueba basadas en coordenadas de
  pantalla usadas en este proyecto; se verificó manualmente que el picker
  se abre, muestra la atribución correcta y devuelve el control a la app
  sin errores, pero no fue posible verificar de punta a punta la
  selección automatizada de una imagen específica sin intervención
  manual — una limitación de la herramienta de pruebas usada, no de la
  app (ver `SAD.md` §9).
