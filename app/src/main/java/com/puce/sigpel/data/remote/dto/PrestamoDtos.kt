package com.puce.sigpel.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espejo de com.puce.sigpel.dto.LoanDtos en el backend (LoanController,
 * @RequestMapping("/loans")). Los Instant llegan como String ISO-8601.
 */
enum class EstadoPrestamo {
    @SerializedName("PENDING") PENDIENTE,
    @SerializedName("APPROVED") APROBADO,
    @SerializedName("REJECTED") RECHAZADO,
    @SerializedName("RETURNED") DEVUELTO
}

data class PrestamoRequest(
    @SerializedName("equipmentId") val equipoId: Long,
    @SerializedName("estimatedReturnDate") val fechaDevolucionEstimada: String? = null
)

data class PrestamoEstadoRequest(
    @SerializedName("status") val estado: EstadoPrestamo,
    @SerializedName("comment") val comentario: String? = null
)

data class PrestamoResponse(
    val id: Long,
    @SerializedName("equipmentId") val equipoId: Long,
    @SerializedName("equipmentName") val equipoNombre: String,
    @SerializedName("studentUser") val estudianteUser: String,
    @SerializedName("requestDate") val fechaSolicitud: String,
    @SerializedName("estimatedReturnDate") val fechaDevolucionEstimada: String?,
    @SerializedName("actualReturnDate") val fechaDevolucionReal: String?,
    @SerializedName("status") val estado: EstadoPrestamo,
    @SerializedName("comment") val comentario: String?
)
