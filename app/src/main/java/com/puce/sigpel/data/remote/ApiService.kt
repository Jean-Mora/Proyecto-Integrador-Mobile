package com.puce.sigpel.data.remote

import com.puce.sigpel.data.remote.dto.CategoriaEquipoRequest
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.data.remote.dto.EquipoEstadoRequest
import com.puce.sigpel.data.remote.dto.EquipoRequest
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import com.puce.sigpel.data.remote.dto.IncidenciaRequest
import com.puce.sigpel.data.remote.dto.IncidenciaResponse
import com.puce.sigpel.data.remote.dto.PrestamoEstadoRequest
import com.puce.sigpel.data.remote.dto.PrestamoRequest
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Refleja 1:1 los controllers del backend (ver Backend/.../controllers). */
interface ApiService {

    // --- Equipos: GET publico, resto requiere rol ENCARGADO ---
    @GET("equipos")
    suspend fun listarEquipos(@Query("estado") estado: EstadoEquipo? = null): List<EquipoResponse>

    @GET("equipos/{id}")
    suspend fun obtenerEquipo(@Path("id") id: Long): EquipoResponse

    @POST("equipos")
    suspend fun crearEquipo(@Body request: EquipoRequest): EquipoResponse

    @PATCH("equipos/{id}")
    suspend fun actualizarEstadoEquipo(@Path("id") id: Long, @Body request: EquipoEstadoRequest): EquipoResponse

    @DELETE("equipos/{id}")
    suspend fun eliminarEquipo(@Path("id") id: Long): Response<Unit>

    // --- Categorias: GET publico, resto requiere rol ENCARGADO ---
    @GET("categorias")
    suspend fun listarCategorias(): List<CategoriaEquipoResponse>

    @POST("categorias")
    suspend fun crearCategoria(@Body request: CategoriaEquipoRequest): CategoriaEquipoResponse

    @PATCH("categorias/{id}")
    suspend fun editarCategoria(@Path("id") id: Long, @Body request: CategoriaEquipoRequest): CategoriaEquipoResponse

    @DELETE("categorias/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Long): Response<Unit>

    // --- Prestamos ---
    @POST("prestamos")
    suspend fun solicitarPrestamo(@Body request: PrestamoRequest): PrestamoResponse

    @GET("prestamos/me")
    suspend fun misPrestamos(): List<PrestamoResponse>

    /**
     * Bandeja de solicitudes para ENCARGADO. Endpoint marcado como pendiente en
     * docs/sigpel_pantallas_moviles.md (hoy solo existe /prestamos/me); se deja
     * declarado para cuando el backend lo exponga.
     */
    @GET("prestamos")
    suspend fun listarPrestamos(): List<PrestamoResponse>

    @PATCH("prestamos/{id}")
    suspend fun cambiarEstadoPrestamo(@Path("id") id: Long, @Body request: PrestamoEstadoRequest): PrestamoResponse

    @DELETE("prestamos/{id}")
    suspend fun cancelarPrestamo(@Path("id") id: Long): Response<Unit>

    // --- Incidencias: todo requiere rol ENCARGADO ---
    @POST("incidencias")
    suspend fun registrarIncidencia(@Body request: IncidenciaRequest): IncidenciaResponse

    @GET("incidencias/{id}")
    suspend fun obtenerIncidencia(@Path("id") id: Long): IncidenciaResponse

    @PATCH("incidencias/{id}")
    suspend fun actualizarIncidencia(@Path("id") id: Long, @Body request: IncidenciaRequest): IncidenciaResponse

    @DELETE("incidencias/{id}")
    suspend fun eliminarIncidencia(@Path("id") id: Long): Response<Unit>
}
