package com.puce.sigpel.data.remote.dto

/** Espejo de com.puce.sigpel.dto.IncidenciaDtos en el backend. */
enum class TipoIncidencia { DANIO, PERDIDA, RETRASO }

data class IncidenciaRequest(
    val prestamoId: Long,
    val tipo: TipoIncidencia,
    val descripcion: String? = null
)

data class IncidenciaResponse(
    val id: Long,
    val prestamoId: Long,
    val tipo: TipoIncidencia,
    val descripcion: String?,
    val fechaReporte: String
)
