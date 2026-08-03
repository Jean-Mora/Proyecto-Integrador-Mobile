# SIGPEL Mobile

App Android nativa (Kotlin) para SIGPEL, implementada según
[`docs/sigpel_pantallas_moviles.md`](docs/sigpel_pantallas_moviles.md): catálogo
público, préstamos de equipos (rol `ESTUDIANTE`) y gestión de equipos,
categorías, solicitudes e incidencias (rol `ENCARGADO`).

## Stack

- Kotlin + View Binding, sin frameworks de DI externos (contenedor manual en `SigpelApp`).
- Navigation Component (`nav_graph.xml`) + `ViewModel`/`LiveData`.
- Retrofit + OkHttp + Gson contra el backend Spring Boot (`Backend/`) y contra
  el endpoint público de Cognito (`InitiateAuth`, flujo `USER_PASSWORD_AUTH`).
- `EncryptedSharedPreferences` para guardar el JWT de sesión.

## Diseño responsive

- `values/` vs `values-sw600dp/` vs `values-sw840dp/`: márgenes, tamaños de
  texto y número de columnas de grilla (`integers.xml`) crecen en tablets.
- `layout/activity_main.xml` (teléfono, `BottomNavigationView`) vs
  `layout-sw600dp/activity_main.xml` (tablet, `NavigationRailView`) — mismo
  `id` (`nav_view`) y mismo menú (`bottom_nav_menu.xml`), sin duplicar lógica
  en `MainActivity`.
- Catálogo y gestión de equipos usan `GridLayoutManager` con `spanCount`
  tomado de `R.integer.grid_columns_*` (1–2 columnas en teléfono, 3–4 en
  tablet).
- Formularios (login, solicitar préstamo, nueva incidencia) están en una
  `MaterialCardView` centrada con `app:layout_constraintWidth_max` para no
  estirarse a todo el ancho en pantallas grandes.

## Configuración antes de compilar

1. Copia `local.properties.example` a `local.properties` y completa:
   - `sdk.dir`: ruta a tu Android SDK.
   - `API_BASE_URL`: URL del backend SIGPEL (`10.0.2.2` apunta al `localhost`
     de tu máquina desde el emulador).
   - `COGNITO_REGION`, `COGNITO_USER_POOL_ID`, `COGNITO_CLIENT_ID`: el mismo
     User Pool que usa el backend como `issuer-uri`
     (`Backend/src/main/resources/application.yml`).
2. Abre la carpeta `Mobile/` en Android Studio (Gradle sync) o compila por
   CLI con `./gradlew assembleDebug`.

## Notas sobre el backend (ver también sección 5 del md)

- `GET /equipos/{id}` y `GET /prestamos` (bandeja completa) ya están
  implementados en `Backend/.../controllers`, así que la app los consume
  directamente en `ApiService`.
- El backend **no** soporta editar nombre/categoría/descripción de un equipo
  existente (`PATCH /equipos/{id}` solo cambia `estado`); por eso la pantalla
  de gestión de equipos solo permite crear, cambiar estado y eliminar.
- `POST /prestamos` no acepta comentario (solo `equipoId` y
  `fechaDevolucionEstimada`); el comentario solo existe al cambiar de estado
  (`PATCH /prestamos/{id}`, usado por el encargado al rechazar).
- Login: se llama directo al endpoint REST de Cognito (sin el SDK completo de
  AWS) para no agregar una dependencia pesada solo por `InitiateAuth`; migrar
  a AWS Amplify Auth sigue pendiente de decisión (punto 5 del md).
