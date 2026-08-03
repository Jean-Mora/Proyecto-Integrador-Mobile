# SIGPEL — Especificación de pantallas móviles

> Proyecto Integrador · NRC 1473 · Jean Pierre Mora, Mateo Rivera
> Base de diseño para implementar en Android Studio (Kotlin). Sin código todavía —
> esto es el mapa de pantallas, endpoints y estructura de paquetes antes de programar.

---

## 1. Roles

| Rol | Descripción | Requiere login |
|---|---|---|
| `VISITANTE` | Explora el catálogo público, sin privilegios. | No |
| `ESTUDIANTE` | Consulta equipos, solicita y gestiona sus propios préstamos. | Sí |
| `ENCARGADO` | Administra equipos, categorías, solicitudes e incidencias. | Sí |

---

## 2. Flujo de navegación

```
SplashActivity
 └─ LoginFragment (Cognito)
     ├─ (VISITANTE, sin login) ─▶ CatalogoFragment ─▶ DetalleEquipoFragment
     │
     ├─ (ESTUDIANTE) ─▶ CatalogoFragment ─▶ DetalleEquipoFragment ─▶ SolicitarPrestamoFragment
     │                └─▶ MisPrestamosFragment ─▶ DetallePrestamoFragment (cancelar)
     │
     └─ (ENCARGADO) ─▶ GestionEquiposFragment (+ categorías)
                     ├─▶ SolicitudesPendientesFragment (aprobar/rechazar)
                     └─▶ NuevaIncidenciaFragment
```

---

## 3. Pantallas

### 3.1 Catálogo (pública)
- **Kotlin:** `CatalogoFragment` + `CatalogoViewModel`
- **Layout:** `fragment_catalogo.xml`
- **Endpoint:** `GET /equipos`, `GET /categorias` (filtro)
- **Acceso:** público
- **Elementos:** buscador, chips de filtro por categoría, `RecyclerView` (thumbnail + nombre + estado del equipo)
- **Navega a:** Detalle de equipo

### 3.2 Login
- **Kotlin:** `LoginFragment` + `AuthViewModel`
- **Layout:** `fragment_login.xml`
- **Endpoint:** Cognito (`InitiateAuth`) — obtiene el JWT
- **Acceso:** público
- **Elementos:** campo usuario, campo contraseña, botón "Iniciar sesión"
- **Nota:** guardar el JWT (por ejemplo en `EncryptedSharedPreferences`) para enviarlo en cada llamada posterior

### 3.3 Detalle de equipo
- **Kotlin:** `DetalleEquipoFragment` + `DetalleEquipoViewModel`
- **Layout:** `fragment_detalle_equipo.xml`
- **Endpoint:** `GET /equipos/{id}` ⚠️ *(agregar a la matriz — hoy solo existe `GET /equipos` sin id)*
- **Acceso:** público (botón "Solicitar préstamo" solo visible si hay sesión de `ESTUDIANTE`)
- **Elementos:** imagen/ícono, nombre, categoría, descripción, estado, botón "Solicitar préstamo"
- **Navega a:** Solicitar préstamo

### 3.4 Solicitar préstamo (ESTUDIANTE)
- **Kotlin:** `SolicitarPrestamoFragment` + `PrestamoViewModel`
- **Layout:** `fragment_solicitar_prestamo.xml`
- **Endpoint:** `POST /prestamos`
- **Acceso:** privado · `ESTUDIANTE`
- **Campos del formulario:** equipo (prellenado), fecha de devolución estimada, comentario
- **Navega a:** Mis préstamos (tras confirmar)

### 3.5 Mis préstamos (ESTUDIANTE)
- **Kotlin:** `MisPrestamosFragment` + `PrestamoViewModel`
- **Layout:** `fragment_mis_prestamos.xml`
- **Endpoint:** `GET /prestamos/me`
- **Acceso:** privado · `ESTUDIANTE` (propiedad)
- **Elementos:** `RecyclerView` de préstamos con badge de estado (`pendiente` / `aprobado` / `rechazado` / `devuelto`)
- **Navega a:** Detalle de préstamo

### 3.6 Detalle de préstamo / cancelar (ESTUDIANTE)
- **Kotlin:** `DetallePrestamoFragment`
- **Layout:** `fragment_detalle_prestamo.xml`
- **Endpoint:** `DELETE /prestamos/{id}`
- **Acceso:** privado · `ESTUDIANTE` + dueño del préstamo (403 si no coincide `estudiante_user` con el JWT)
- **Elementos:** datos del préstamo, botón "Cancelar solicitud" (solo si `estado == pendiente`)

### 3.7 Gestión de equipos y categorías (ENCARGADO)
- **Kotlin:** `GestionEquiposFragment` + `GestionEquiposViewModel` (y análogo `GestionCategoriasFragment`)
- **Layout:** `fragment_gestion_equipos.xml`
- **Endpoint:** `POST / GET / PATCH / DELETE /equipos` y `/categorias`
- **Acceso:** privado · `ENCARGADO`
- **Elementos:** lista con ícono editar/eliminar por fila, botón flotante "+" para crear

### 3.8 Solicitudes pendientes (ENCARGADO)
- **Kotlin:** `SolicitudesPendientesFragment` + `PrestamoAdminViewModel`
- **Layout:** `fragment_solicitudes_pendientes.xml`
- **Endpoint:** `GET /prestamos` ⚠️ *(agregar a la matriz — hoy solo existe `GET /prestamos/me` para el estudiante)* + `PATCH /prestamos/{id}`
- **Acceso:** privado · `ENCARGADO`
- **Elementos:** lista de solicitudes con botones "Aprobar" / "Rechazar" por fila

### 3.9 Registrar incidencia (ENCARGADO)
- **Kotlin:** `NuevaIncidenciaFragment` + `IncidenciaViewModel` (y `GestionIncidenciasFragment` para listar/editar)
- **Layout:** `fragment_nueva_incidencia.xml`
- **Endpoint:** `POST /incidencias` (y `GET/PATCH/DELETE /incidencias` para la gestión)
- **Acceso:** privado · `ENCARGADO`
- **Campos del formulario:** préstamo asociado, tipo de incidencia, descripción

---

## 4. Estructura de paquetes sugerida

```
com.puce.sigpel
├── data
│   ├── remote/          // ApiService (Retrofit), DTOs de request/response
│   ├── auth/            // CognitoManager, manejo y refresco del JWT
│   └── repository/      // EquipoRepository, PrestamoRepository, IncidenciaRepository
├── ui
│   ├── auth/            // LoginFragment
│   ├── catalogo/        // CatalogoFragment, DetalleEquipoFragment
│   ├── prestamos/       // SolicitarPrestamoFragment, MisPrestamosFragment, DetallePrestamoFragment
│   ├── encargado/       // GestionEquiposFragment, SolicitudesPendientesFragment, NuevaIncidenciaFragment
│   └── common/          // vistas y componentes compartidos (badges de estado, etc.)
└── SigpelApp.kt
```

---

## 5. Pendientes antes de programar

- [ ] Añadir `GET /equipos/{id}` a la matriz de endpoints (detalle de equipo).
- [ ] Añadir `GET /prestamos` (todas, rol `ENCARGADO`) a la matriz de endpoints (bandeja de solicitudes).
- [ ] Definir el SDK de Cognito a usar en Android (AWS Amplify Auth vs `aws-android-sdk-cognitoidentityprovider` directo).
- [ ] Confirmar librería de networking (Retrofit + OkHttp recomendado) e interceptor para adjuntar el JWT.
- [ ] Definir estrategia de refresh token / expiración de sesión.
