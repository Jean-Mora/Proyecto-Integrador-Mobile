package com.puce.sigpel.data.repository

import com.puce.sigpel.data.remote.ApiService
import com.puce.sigpel.data.remote.dto.EquipoEstadoRequest
import com.puce.sigpel.data.remote.dto.EquipoRequest
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import okhttp3.MultipartBody

/** Pantallas 3.1, 3.3 (catalogo publico) y 3.7 (gestion, rol ENCARGADO). */
class EquipoRepository(private val apiService: ApiService) {

    suspend fun listar(estado: EstadoEquipo? = null): Result<List<EquipoResponse>> = runCatching {
        apiService.listarEquipos(estado)
    }

    suspend fun obtener(id: Long): Result<EquipoResponse> = runCatching {
        apiService.obtenerEquipo(id)
    }

    suspend fun crear(request: EquipoRequest): Result<EquipoResponse> = runCatching {
        apiService.crearEquipo(request)
    }

    suspend fun actualizarEstado(id: Long, estado: EstadoEquipo): Result<EquipoResponse> = runCatching {
        apiService.actualizarEstadoEquipo(id, EquipoEstadoRequest(estado))
    }

    suspend fun eliminar(id: Long): Result<Unit> = runCatching {
        apiService.eliminarEquipo(id)
        Unit
    }

    suspend fun subirImagen(id: Long, file: MultipartBody.Part): Result<EquipoResponse> = runCatching {
        apiService.subirImagenEquipo(id, file)
    }
}
