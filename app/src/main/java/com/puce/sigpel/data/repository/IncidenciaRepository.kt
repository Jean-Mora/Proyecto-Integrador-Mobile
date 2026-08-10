package com.puce.sigpel.data.repository

import com.puce.sigpel.data.remote.ApiService
import com.puce.sigpel.data.remote.dto.IncidenciaRequest
import com.puce.sigpel.data.remote.dto.IncidenciaResponse
import com.puce.sigpel.data.remote.dto.TipoIncidencia

/** Pantalla 3.9 (rol ENCARGADO). */
class IncidenciaRepository(private val apiService: ApiService) {

    suspend fun listar(): Result<List<IncidenciaResponse>> = runCatching {
        apiService.listarIncidencias()
    }

    suspend fun registrar(prestamoId: Long, tipo: TipoIncidencia, descripcion: String?): Result<IncidenciaResponse> = runCatching {
        apiService.registrarIncidencia(IncidenciaRequest(prestamoId, tipo, descripcion))
    }

    suspend fun obtener(id: Long): Result<IncidenciaResponse> = runCatching {
        apiService.obtenerIncidencia(id)
    }

    suspend fun actualizar(id: Long, prestamoId: Long, tipo: TipoIncidencia, descripcion: String?): Result<IncidenciaResponse> = runCatching {
        apiService.actualizarIncidencia(id, IncidenciaRequest(prestamoId, tipo, descripcion))
    }

    suspend fun eliminar(id: Long): Result<Unit> = runCatching {
        apiService.eliminarIncidencia(id)
        Unit
    }
}
