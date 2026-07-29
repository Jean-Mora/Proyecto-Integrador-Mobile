package com.puce.sigpel.data.repository

import com.puce.sigpel.data.auth.SessionManager
import com.puce.sigpel.data.remote.ApiService
import com.puce.sigpel.data.remote.dto.EstadoPrestamo
import com.puce.sigpel.data.remote.dto.PrestamoEstadoRequest
import com.puce.sigpel.data.remote.dto.PrestamoRequest
import com.puce.sigpel.data.remote.dto.PrestamoResponse

/** Pantallas 3.4, 3.5, 3.6 (ESTUDIANTE) y 3.8 (bandeja ENCARGADO). */
class PrestamoRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    val currentUsername: String? get() = sessionManager.username

    suspend fun solicitar(equipoId: Long, fechaDevolucionEstimadaIso: String?): Result<PrestamoResponse> = runCatching {
        apiService.solicitarPrestamo(PrestamoRequest(equipoId, fechaDevolucionEstimadaIso))
    }

    suspend fun misPrestamos(): Result<List<PrestamoResponse>> = runCatching {
        apiService.misPrestamos()
    }

    suspend fun listarTodos(): Result<List<PrestamoResponse>> = runCatching {
        apiService.listarPrestamos()
    }

    suspend fun cambiarEstado(id: Long, estado: EstadoPrestamo, comentario: String? = null): Result<PrestamoResponse> = runCatching {
        apiService.cambiarEstadoPrestamo(id, PrestamoEstadoRequest(estado, comentario))
    }

    suspend fun cancelar(id: Long): Result<Unit> = runCatching {
        apiService.cancelarPrestamo(id)
        Unit
    }
}
