# Sigpel — Mobile

App Android nativa (Kotlin) del proyecto integrador **Sigpel** (préstamo de
equipos de laboratorio): catálogo público, préstamos de equipos (rol
`ESTUDIANTE`) y gestión de equipos, categorías, solicitudes e incidencias
(rol `ENCARGADO`), contra el mismo backend Spring Boot + AWS Cognito.

> **Estado:** `main` se mantiene sin código hasta el primer release formal. El
> desarrollo avanza en `develop`, historia de usuario por historia de usuario,
> cada una en su propia rama `feature/HU-XX` con su propio Pull Request.

## Stack

- Kotlin + View Binding, sin frameworks de DI externos (contenedor manual en `SigpelApp`).
- Navigation Component (`nav_graph.xml`) + `ViewModel`/`LiveData`.
- Retrofit + OkHttp + Gson contra el backend Spring Boot y contra el
  endpoint público de Cognito (`InitiateAuth`, flujo `USER_PASSWORD_AUTH`).
- `EncryptedSharedPreferences` para guardar el JWT de sesión.

## Configuración antes de compilar

1. Copia `local.properties.example` a `local.properties` y completa:
   - `sdk.dir`: ruta a tu Android SDK.
   - `API_BASE_URL`: URL del backend Sigpel.
   - `COGNITO_REGION`, `COGNITO_USER_POOL_ID`, `COGNITO_CLIENT_ID`: el mismo
     User Pool que usa el backend como `issuer-uri`.
2. Abre la carpeta en Android Studio (Gradle sync) o compila por CLI con
   `./gradlew assembleDebug`.

Documentación de decisiones: ver `docs/adr/`.
