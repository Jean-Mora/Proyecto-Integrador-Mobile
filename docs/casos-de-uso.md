# Especificación de Casos de Uso

**Sistema:** SIGPEL Móvil — Cliente Android
**Integrantes:** Jean Pierre Mora Santillán, Luis Mateo Rivera Escalante
**Versión:** 1.0 — 2026-08-09

> **Nota de trazabilidad:** cada paso de estos casos de uso referencia un
> Requisito Funcional (**RF-XXX**) del [`SRS.md`](SRS.md) de este mismo
> repositorio. Cuando un paso depende de una regla de negocio aplicada
> por el backend (no por la app), se referencia además el `RF-XXX`
> correspondiente de
> [`Backend/docs/SRS.md`](../../Backend/docs/SRS.md), marcado como
> **(backend)**.

## Índice

| CU | Nombre | Actor | RF |
|---|---|---|---|
| [CU-01](#cu-01-iniciar-sesión) | Iniciar sesión | Estudiante / Encargado | RF-001, RF-002, RF-003 |
| [CU-02](#cu-02-explorar-el-catálogo-público) | Explorar el catálogo público | Visitante | RF-006, RF-007, RF-008 |
| [CU-03](#cu-03-solicitar-préstamo) | Solicitar préstamo | Estudiante | RF-009, RF-010, RF-011 |
| [CU-04](#cu-04-cancelar-préstamo) | Cancelar préstamo | Estudiante | RF-013 |
| [CU-05](#cu-05-aprobar-o-rechazar-solicitud-de-préstamo) | Aprobar o rechazar solicitud de préstamo | Encargado | RF-015 |
| [CU-06](#cu-06-marcar-préstamo-como-devuelto) | Marcar préstamo como devuelto | Encargado | RF-016 |
| [CU-07](#cu-07-registrar-equipo-con-imagen) | Registrar equipo con imagen | Encargado | RF-018, RF-019, ADR-0004 |
| [CU-08](#cu-08-cambiar-el-estado-de-un-equipo) | Cambiar el estado de un equipo | Encargado | RF-021 |
| [CU-09](#cu-09-eliminar-equipo) | Eliminar equipo | Encargado | RF-022 |
| [CU-10](#cu-10-gestionar-categorías-de-equipo) | Gestionar categorías de equipo | Encargado | RF-023, RF-024 |
| [CU-11](#cu-11-registrar-incidencia) | Registrar incidencia | Encargado | RF-017 |
| [CU-12](#cu-12-gestionar-incidencias) | Gestionar incidencias | Encargado | RF-025, RF-026 |
| [CU-13](#cu-13-cerrar-sesión) | Cerrar sesión | Estudiante / Encargado | RF-004 |

*(Se excluyen del listado las operaciones de solo lectura sin flujo
alterno relevante que documentar — ver detalle del equipo, listar mis
préstamos, listar solicitudes, listar equipos, listar categorías, listar
incidencias — cuyo comportamiento queda cubierto por RNF-04/RNF-05 del
`SRS.md`.)*

---

## CU-01: Iniciar sesión

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-01 — Iniciar sesión |
| **Actor Principal** | Estudiante / Encargado |
| **Precondiciones** | El actor tiene una cuenta en el User Pool de AWS Cognito, perteneciente al grupo `ESTUDIANTE` o `ENCARGADO` (RF-001, backend: RF-001, RF-002). |
| **Flujo Básico** | 1. El actor abre `LoginFragment` desde el catálogo o al tocar "Iniciar sesión" en la barra superior.<br>2. El actor ingresa usuario y contraseña y toca "Ingresar".<br>3. La app envía las credenciales directamente a AWS Cognito (`POST InitiateAuth`, flujo `USER_PASSWORD_AUTH`) (RF-001).<br>4. Cognito valida las credenciales y devuelve un `idToken`/`accessToken`.<br>5. La app decodifica el claim `cognito:groups` del `idToken` para derivar el rol (RF-002).<br>6. La app persiste la sesión cifrada en `EncryptedSharedPreferences` (RF-003).<br>7. La app navega al catálogo, ahora con las pestañas/botones del rol correspondiente visibles. |
| **Flujos Alternos** | 3a. El actor toca "Continuar como visitante" en vez de ingresar credenciales → la app navega al catálogo sin iniciar sesión (rol `VISITANTE`). |
| **Excepciones** | **E1.** Usuario o contraseña vacíos → la app muestra "Completa usuario y contraseña" sin llamar a Cognito. <br>**E2.** Cognito rechaza las credenciales → la app muestra el mensaje de error devuelto por Cognito, o "No se pudo iniciar sesión. Verifica tus credenciales." si no puede interpretarlo. <br>**E3.** Sin conectividad → la petición falla y la app muestra el mismo mensaje de error genérico (la app no distingue "sin red" de "credenciales inválidas" en esta versión). |
| **Postcondiciones** | El actor tiene una sesión activa con rol resuelto; la app puede adjuntar `Authorization: Bearer <idToken>` en peticiones subsecuentes a la API SIGPEL. |

---

## CU-02: Explorar el catálogo público

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-02 — Explorar el catálogo público |
| **Actor Principal** | Visitante (también accesible por Estudiante y Encargado) |
| **Precondiciones** | Ninguna — no requiere sesión (RF-006). |
| **Flujo Básico** | 1. El actor abre la app; `CatalogoFragment` es el destino de inicio del grafo de navegación.<br>2. La app pide el catálogo de equipos y, en paralelo, la lista de categorías para los chips de filtro (backend: RF-005, RF-009).<br>3. El actor escribe texto en el buscador y/o toca un chip de categoría (RF-007).<br>4. La app filtra la lista mostrada localmente combinando texto y categoría seleccionada.<br>5. El actor toca un equipo → la app navega a `DetalleEquipoFragment` mostrando su información completa (RF-008). |
| **Flujos Alternos** | 3a. El actor no aplica ningún filtro → se muestra el catálogo completo. |
| **Excepciones** | **E1.** La petición al backend falla (sin red, servidor caído) → la app muestra el estado de error de `CatalogoViewModel` con el mensaje correspondiente, sin *crash* (RNF-04). <br>**E2.** El filtro no encuentra resultados → la app muestra el estado vacío "No se encontraron equipos con esos filtros" (RNF-05), distinto del estado de error. |
| **Postcondiciones** | El actor puede ver el catálogo actualizado y, si lo desea, continuar a solicitar sesión o al detalle de un equipo. |

---

## CU-03: Solicitar préstamo

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-03 — Solicitar préstamo |
| **Actor Principal** | Estudiante |
| **Precondiciones** | Sesión de Estudiante activa (CU-01); el equipo consultado está en estado `Disponible` (backend: RF-017). |
| **Flujo Básico** | 1. El actor, con sesión de Estudiante, abre el detalle de un equipo disponible; el botón "Solicitar préstamo" es visible (RF-009).<br>2. El actor lo toca → la app navega a `SolicitarPrestamoFragment` con el equipo preseleccionado.<br>3. El actor, opcionalmente, elige una fecha estimada de devolución mediante el selector de fecha, y agrega un comentario opcional.<br>4. El actor toca "Confirmar solicitud".<br>5. La app envía `POST /loans` con el `equipoId`, la fecha (si se eligió) y el comentario (RF-010).<br>6. El backend responde `201 Created` con el préstamo en estado `PENDIENTE`.<br>7. La app navega a `MisPrestamosFragment`, limpiando la pila de navegación hasta el catálogo (RF-011). |
| **Flujos Alternos** | 3a. El actor no selecciona fecha → el campo queda vacío y se envía como opcional (backend: RF-018, no es obligatoria). |
| **Excepciones** | **E1.** El equipo dejó de estar disponible entre que se cargó el detalle y se confirmó la solicitud → el backend responde `409 Conflict` (backend: RF-019) y la app muestra "No se pudo enviar la solicitud" sin navegar. <br>**E2.** Fecha de devolución anterior al momento actual → el backend responde `400 Bad Request` (backend: RF-018) y la app muestra el mensaje de error correspondiente. |
| **Postcondiciones** | Existe un nuevo préstamo en estado `PENDIENTE` asociado al Estudiante; visible de inmediato en "Mis préstamos". |

---

## CU-04: Cancelar préstamo

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-04 — Cancelar préstamo |
| **Actor Principal** | Estudiante |
| **Precondiciones** | El préstamo pertenece al Estudiante autenticado y su estado es `PENDIENTE` (backend: RF-023). |
| **Flujo Básico** | 1. El actor abre "Mis préstamos" y selecciona un préstamo pendiente → navega a `DetallePrestamoFragment`.<br>2. Al ser `PENDIENTE`, la app muestra el botón "Cancelar solicitud" (RF-013).<br>3. El actor lo toca → la app muestra un diálogo de confirmación ("¿Cancelar solicitud? Esta acción no se puede deshacer").<br>4. El actor confirma → la app envía `DELETE /loans/{id}`.<br>5. El backend responde `204 No Content`.<br>6. La app muestra "Solicitud cancelada" y refresca el estado del préstamo en pantalla. |
| **Flujos Alternos** | 3a. El actor cierra el diálogo sin confirmar → no ocurre ningún cambio. |
| **Excepciones** | **E1.** El préstamo ya no es `PENDIENTE` (p. ej. fue aprobado en el intervalo) → el backend responde `403 Forbidden` (backend: RF-023, validación de propiedad/estado) y la app muestra el error sin modificar la pantalla. |
| **Postcondiciones** | El préstamo queda en estado `RECHAZADO`/cancelado según lo determine el backend; deja de mostrar el botón de cancelar. |

---

## CU-05: Aprobar o rechazar solicitud de préstamo

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-05 — Aprobar o rechazar solicitud de préstamo |
| **Actor Principal** | Encargado |
| **Precondiciones** | Sesión de Encargado activa; existe al menos una solicitud en estado `PENDIENTE` (RF-014). |
| **Flujo Básico** | 1. El actor abre la pestaña "Solicitudes" (`SolicitudesPendientesFragment`).<br>2. La app lista todas las solicitudes del sistema, con acciones "Aprobar"/"Rechazar" visibles solo en las de estado `PENDIENTE` (RF-015).<br>3a. El actor toca "Aprobar" → la app envía `PATCH /loans/{id}` con el nuevo estado `APROBADO` directamente. <br>3b. El actor toca "Rechazar" → la app abre un diálogo para un comentario opcional, y al confirmar envía `PATCH /loans/{id}` con estado `RECHAZADO` y el comentario.<br>4. El backend responde `200 OK` con el préstamo actualizado y registra el cambio en la bitácora de auditoría (backend: RF-024).<br>5. La app recarga la lista completa de solicitudes. |
| **Flujos Alternos** | Ninguno adicional a los dos caminos ya descritos en el paso 3. |
| **Excepciones** | **E1.** La solicitud cambió de estado por otra vía entre que se listó y se accionó → el backend responde con un error de transición inválida y la app muestra el mensaje devuelto mediante un *toast*, sin modificar la lista hasta el siguiente refresco. |
| **Postcondiciones** | La solicitud queda en estado `APROBADO` o `RECHAZADO`; si fue aprobada, el equipo asociado pasa a `PRESTADO` (regla del backend, no de la app). |

---

## CU-06: Marcar préstamo como devuelto

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-06 — Marcar préstamo como devuelto |
| **Actor Principal** | Encargado |
| **Precondiciones** | El préstamo está en estado `APROBADO` (RF-016). |
| **Flujo Básico** | 1. En "Solicitudes", el actor localiza un préstamo `APROBADO`; el botón "Marcar como devuelto" es visible solo en ese estado.<br>2. El actor lo toca.<br>3. La app envía `PATCH /loans/{id}` con estado `DEVUELTO`.<br>4. El backend responde `200 OK` y actualiza el equipo asociado a `Disponible` (regla del backend).<br>5. La app recarga la lista; el botón "Registrar incidencia" ahora se vuelve visible para ese préstamo (RF-017). |
| **Flujos Alternos** | Ninguno. |
| **Excepciones** | **E1.** Error de red al confirmar → la app muestra el error mediante *toast*, sin cambiar el estado mostrado hasta un nuevo refresco exitoso. |
| **Postcondiciones** | El préstamo queda `DEVUELTO`; se habilita el registro de incidencias sobre él (CU-11). |

---

## CU-07: Registrar equipo con imagen

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-07 — Registrar equipo con imagen |
| **Actor Principal** | Encargado |
| **Precondiciones** | Sesión de Encargado activa; existe al menos una categoría creada (CU-10). |
| **Flujo Básico** | 1. El actor abre "Equipos" → toca el botón flotante "+" → navega a `RegistrarEquipoFragment`.<br>2. El actor toca el recuadro de imagen o "Agregar imagen (opcional)" → se abre el Photo Picker del sistema (RF-019, ADR-0004).<br>3. El actor selecciona una foto de la galería; la app la muestra en la vista previa.<br>4. El actor selecciona una categoría del menú desplegable, y escribe nombre, número de serie y descripción opcional.<br>5. El actor toca "Guardar".<br>6. La app valida en el cliente que categoría, nombre y número de serie no estén vacíos (RF-018); si falta alguno, muestra el error correspondiente y no envía la petición.<br>7. La app envía `POST /equipment` con los datos del formulario.<br>8. El backend responde `201 Created` con el equipo y su `id`.<br>9. Si se seleccionó una imagen, la app la convierte a `multipart/form-data` y envía `POST /equipment/{id}/image` (RF-019).<br>10. La app muestra "Equipo registrado correctamente" y limpia el formulario para un nuevo registro. |
| **Flujos Alternos** | 2a. El actor no selecciona ninguna imagen → el equipo se crea sin `imageUrl`, sin ejecutar el paso 9. |
| **Excepciones** | **E1.** El número de serie ya existe en el sistema → el backend responde `409 Conflict` (backend: RF-011, ADR-0006 del backend) y la app muestra el mensaje de error sin limpiar el formulario, para que el actor pueda corregirlo. <br>**E2.** Falla la subida de la imagen tras crear el equipo exitosamente → el equipo queda creado sin imagen; la app reporta el error de la subida (el equipo no se revierte). |
| **Postcondiciones** | Existe un nuevo equipo, visible de inmediato en el catálogo público y en "Gestión de equipos", con imagen si la subida fue exitosa. |

---

## CU-08: Cambiar el estado de un equipo

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-08 — Cambiar el estado de un equipo |
| **Actor Principal** | Encargado |
| **Precondiciones** | Sesión de Encargado activa; existe al menos un equipo registrado. |
| **Flujo Básico** | 1. El actor abre "Equipos" (`GestionEquiposFragment`), que lista todos los equipos con su estado actual.<br>2. El actor toca el ícono de editar de un equipo.<br>3. La app abre un diálogo de selección única con las tres opciones de estado (`Disponible`/`Prestado`/`Mantenimiento`), preseleccionando el estado actual (RF-021).<br>4. El actor elige una opción distinta y toca "Guardar".<br>5. La app envía `PATCH /equipment/{id}` con el nuevo estado.<br>6. El backend responde `200 OK`.<br>7. La app recarga la lista, mostrando el badge de estado actualizado con su color correspondiente. |
| **Flujos Alternos** | 4a. El actor selecciona el mismo estado ya vigente y confirma → la petición se envía igual y no cambia nada visible. <br>4b. El actor toca "Cancelar" → el diálogo se cierra sin cambios. |
| **Excepciones** | **E1.** Error de red al guardar → la app muestra el error mediante *toast*, sin alterar la lista mostrada. |
| **Postcondiciones** | El equipo refleja el nuevo estado en el catálogo público y en la propia lista de gestión. |

---

## CU-09: Eliminar equipo

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-09 — Eliminar equipo |
| **Actor Principal** | Encargado |
| **Precondiciones** | Sesión de Encargado activa; el equipo no tiene historial de préstamos asociado (backend: RF-016). |
| **Flujo Básico** | 1. En "Equipos", el actor toca el ícono de eliminar de un equipo.<br>2. La app muestra un diálogo de confirmación con el nombre del equipo.<br>3. El actor confirma.<br>4. La app envía `DELETE /equipment/{id}`.<br>5. El backend responde `204 No Content`.<br>6. La app recarga la lista, sin el equipo eliminado. |
| **Flujos Alternos** | 3a. El actor cancela el diálogo → no ocurre ningún cambio. |
| **Excepciones** | **E1.** El equipo tiene historial de préstamos → el backend responde `409 Conflict` (backend §9, regla de integridad referencial) y la app muestra el mensaje de error mediante *toast*, sin quitar el equipo de la lista. |
| **Postcondiciones** | El equipo deja de existir en el sistema (o permanece intacto si la eliminación fue rechazada por el backend). |

---

## CU-10: Gestionar categorías de equipo

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-10 — Gestionar categorías de equipo |
| **Actor Principal** | Encargado |
| **Precondiciones** | Sesión de Encargado activa. |
| **Flujo Básico** | 1. El actor navega a "Categorías" desde el enlace superior de "Registrar equipo" o de "Equipos".<br>2a. **Crear:** el actor toca el botón flotante "+", ingresa un nombre en el diálogo y confirma → `POST /categories`.<br>2b. **Editar:** el actor toca el ícono de editar de una categoría, cambia el nombre en el mismo diálogo y confirma → `PATCH /categories/{id}`.<br>2c. **Eliminar:** el actor toca el ícono de eliminar, confirma en el diálogo de confirmación → `DELETE /categories/{id}`.<br>3. En los tres casos, el backend responde con éxito y la app recarga la lista de categorías. |
| **Flujos Alternos** | Ninguno adicional a los tres caminos del paso 2. |
| **Excepciones** | **E1.** Se intenta crear/editar con un nombre que ya existe (sin distinguir mayúsculas/minúsculas) → el backend responde `409 Conflict` (backend: RF-006, RF-007) y la app muestra el error sin cerrar el diálogo. <br>**E2.** Se intenta eliminar una categoría con equipos asociados → el backend responde `409 Conflict` (backend: RF-008) y la app lo reporta mediante *toast*. |
| **Postcondiciones** | El catálogo de categorías queda actualizado; toda pantalla que dependa de él (registrar equipo, filtros del catálogo) lo refleja en su siguiente carga. |

---

## CU-11: Registrar incidencia

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-11 — Registrar incidencia |
| **Actor Principal** | Encargado |
| **Precondiciones** | El préstamo sobre el que se reporta la incidencia está en estado `DEVUELTO` (RF-017). |
| **Flujo Básico** | 1. En "Solicitudes", el actor localiza un préstamo `DEVUELTO`; el botón "Registrar Incidencia" es visible.<br>2. El actor lo toca → se abre un diálogo con un campo de comentario/descripción.<br>3. El actor describe la incidencia y confirma.<br>4. La app envía `POST /incidents` con el `loanId`, tipo `DAÑO` (valor por defecto de este flujo) y la descripción.<br>5. El backend responde `201 Created`.<br>6. La app recarga la lista de solicitudes. |
| **Flujos Alternos** | Ninguno — esta versión del flujo de registro rápido desde "Solicitudes" no permite elegir el tipo de incidencia (Daño/Pérdida/Retraso); siempre registra tipo `DAÑO`. Cambiar el tipo requiere editarla posteriormente en el backend (no expuesto en esta app, ver `SAD.md` §9). |
| **Excepciones** | **E1.** Error de red al registrar → la app muestra el error mediante *toast*, sin cerrar el diálogo de forma silenciosa (el actor puede reintentar). |
| **Postcondiciones** | Queda registrada una nueva incidencia asociada al préstamo; visible de inmediato en "Gestión de incidencias" (CU-12). Un mismo préstamo puede acumular varias incidencias (backend: RF-026), verificado en esta iteración registrando dos incidencias reales sobre el mismo préstamo en producción. |

---

## CU-12: Gestionar incidencias

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-12 — Gestionar incidencias |
| **Actor Principal** | Encargado |
| **Precondiciones** | Sesión de Encargado activa. |
| **Flujo Básico** | 1. El actor toca el enlace "Incidencias" en la parte superior de "Solicitudes" → navega a `GestionIncidenciasFragment`.<br>2. La app pide `GET /incidents` y lista todas las incidencias registradas en el sistema, ordenadas por fecha de reporte descendente, con el préstamo asociado, tipo y descripción (RF-025).<br>3. El actor toca el ícono de eliminar de una incidencia.<br>4. La app muestra un diálogo de confirmación.<br>5. El actor confirma → la app envía `DELETE /incidents/{id}`.<br>6. El backend responde `204 No Content`.<br>7. La app recarga la lista. |
| **Flujos Alternos** | 3a. El actor cancela el diálogo de confirmación → no ocurre ningún cambio. |
| **Excepciones** | **E1.** No hay incidencias registradas → la app muestra el estado vacío "No hay incidencias registradas" (RNF-05), sin ser un error. <br>**E2.** El endpoint `GET /incidents` no está disponible en el backend consultado → la app muestra el estado de error "Error del servidor, intenta más tarde" (RNF-04). Este caso ocurrió realmente durante el desarrollo: la pantalla se construyó antes de que el endpoint existiera en producción, y se verificó que el manejo de error funcionara correctamente mientras se completaba el despliegue del backend. |
| **Postcondiciones** | La lista de incidencias refleja el estado real del sistema; una incidencia eliminada deja de contarse en el historial del préstamo asociado. |

---

## CU-13: Cerrar sesión

| Campo | Detalle |
|---|---|
| **ID / Nombre** | CU-13 — Cerrar sesión |
| **Actor Principal** | Estudiante / Encargado |
| **Precondiciones** | El actor tiene una sesión activa. |
| **Flujo Básico** | 1. El actor toca "Cerrar sesión" en la barra superior, disponible desde cualquier pantalla.<br>2. La app muestra un diálogo de confirmación.<br>3. El actor confirma.<br>4. La app borra la sesión persistida (`SessionManager.clear()`).<br>5. La app recalcula la navegación disponible (rol `VISITANTE`) y, si la pantalla actual no es de acceso público, navega al catálogo. |
| **Flujos Alternos** | 3a. El actor cancela el diálogo → la sesión permanece activa. |
| **Excepciones** | Ninguna — el cierre de sesión es una operación local, no depende de una petición de red. |
| **Postcondiciones** | El JWT persistido se elimina del dispositivo; cualquier petición subsecuente a la API se envía sin `Authorization`, recibiendo `401` en los endpoints protegidos si se intentaran igualmente. |

---

## Notas de alcance

- Los 13 casos de uso cubren la totalidad de las pantallas con efectos
  secundarios (creación/modificación/eliminación) de la app. Las
  pantallas de solo lectura (catálogo de equipos ya cubierto en CU-02,
  detalle de equipo, listas de mis préstamos/solicitudes/equipos/
  categorías/incidencias) no se documentan como casos de uso adicionales
  por no tener flujos alternos ni excepciones de negocio propias más allá
  de los estados de carga/vacío/error ya cubiertos por RNF-04 y RNF-05
  del `SRS.md`.
- CU-07, CU-08, CU-09, CU-11 y CU-12 corresponden a las dos pantallas que
  se agregaron en esta iteración (Gestión de Equipos y Gestión de
  Incidencias) al detectar que la especificación original del proyecto
  las pedía y no existían — ver `SAD.md` §9 y la Matriz de Trazabilidad
  del `SRS.md`.
