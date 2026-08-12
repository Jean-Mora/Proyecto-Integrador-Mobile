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
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Refleja 1:1 los controllers del backend (ver Backend/.../controllers). El backend usa
 * rutas y nombres de parametros en ingles (EquipmentController, EquipmentCategoryController,
 * LoanController, IncidentController); los nombres de las funciones/DTOs en Kotlin se dejan
 * en espanol (usados en toda la UI) y el mapeo del wire se hace aqui + en los DTOs.
 */
interface ApiService {

    // --- Equipment (/equipment): GET publico, resto requiere rol ENCARGADO ---
    @GET("equipment")
    suspend fun listarEquipos(@Query("status") estado: EstadoEquipo? = null): List<EquipoResponse>

    @GET("equipment/{id}")
    suspend fun obtenerEquipo(@Path("id") id: Long): EquipoResponse

    @POST("equipment")
    suspend fun crearEquipo(@Body request: EquipoRequest): EquipoResponse

    @PATCH("equipment/{id}")
    suspend fun actualizarEstadoEquipo(@Path("id") id: Long, @Body request: EquipoEstadoRequest): EquipoResponse

    @DELETE("equipment/{id}")
    suspend fun eliminarEquipo(@Path("id") id: Long): Response<Unit>

    @Multipart
    @POST("equipment/{id}/image")
    suspend fun subirImagenEquipo(@Path("id") id: Long, @Part file: MultipartBody.Part): EquipoResponse

    // --- Categories (/categories): GET publico, resto requiere rol ENCARGADO ---
    @GET("categories")
    suspend fun listarCategorias(): List<CategoriaEquipoResponse>

    @POST("categories")
    suspend fun crearCategoria(@Body request: CategoriaEquipoRequest): CategoriaEquipoResponse

    @PATCH("categories/{id}")
    suspend fun editarCategoria(@Path("id") id: Long, @Body request: CategoriaEquipoRequest): CategoriaEquipoResponse

    @DELETE("categories/{id}")
    suspend fun eliminarCategoria(@Path("id") id: Long): Response<Unit>

    // --- Loans (/loans) ---
    @POST("loans")
    suspend fun solicitarPrestamo(@Body request: PrestamoRequest): PrestamoResponse

    @GET("loans/me")
    suspend fun misPrestamos(): List<PrestamoResponse>

    /** Bandeja completa para ENCARGADO (LoanController.listAll, @PreAuthorize ENCARGADO). */
    @GET("loans")
    suspend fun listarPrestamos(): List<PrestamoResponse>

    @PATCH("loans/{id}")
    suspend fun cambiarEstadoPrestamo(@Path("id") id: Long, @Body request: PrestamoEstadoRequest): PrestamoResponse

    @DELETE("loans/{id}")
    suspend fun cancelarPrestamo(@Path("id") id: Long): Response<Unit>

    // --- Incidents (/incidents): todo requiere rol ENCARGADO ---
    @GET("incidents")
    suspend fun listarIncidencias(): List<IncidenciaResponse>

    @POST("incidents")
    suspend fun registrarIncidencia(@Body request: IncidenciaRequest): IncidenciaResponse

    @GET("incidents/{id}")
    suspend fun obtenerIncidencia(@Path("id") id: Long): IncidenciaResponse

    @PATCH("incidents/{id}")
    suspend fun actualizarIncidencia(@Path("id") id: Long, @Body request: IncidenciaRequest): IncidenciaResponse

    @DELETE("incidents/{id}")
    suspend fun eliminarIncidencia(@Path("id") id: Long): Response<Unit>
}
