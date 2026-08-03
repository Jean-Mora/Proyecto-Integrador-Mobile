package com.puce.sigpel.data.remote.dto

/** Espejo de com.puce.sigpel.dto.PrestamoDtos en el backend. Los Instant llegan como String ISO-8601. */
enum class EstadoPrestamo { PENDIENTE, APROBADO, RECHAZADO, DEVUELTO }

data class PrestamoRequest(
    val equipoId: Long,
    val fechaDevolucionEstimada: String? = null
)

data class PrestamoEstadoRequest(
    val estado: EstadoPrestamo,
    val comentario: String? = null
)

data class PrestamoResponse(
    val id: Long,
    val equipoId: Long,
    val equipoNombre: String,
    val estudianteUser: String,
    val fechaSolicitud: String,
    val fechaDevolucionEstimada: String?,
    val fechaDevolucionReal: String?,
    val estado: EstadoPrestamo,
    val comentario: String?
)
