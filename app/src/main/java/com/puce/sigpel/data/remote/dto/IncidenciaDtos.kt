package com.puce.sigpel.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Espejo de com.puce.sigpel.dto.IncidentDtos en el backend (@RequestMapping("/incidents")). */
enum class TipoIncidencia {
    @SerializedName("DAMAGE") DANIO,
    @SerializedName("LOSS") PERDIDA,
    @SerializedName("DELAY") RETRASO
}

data class IncidenciaRequest(
    @SerializedName("loanId") val prestamoId: Long,
    @SerializedName("type") val tipo: TipoIncidencia,
    @SerializedName("description") val descripcion: String? = null
)

data class IncidenciaResponse(
    val id: Long,
    @SerializedName("loanId") val prestamoId: Long,
    @SerializedName("type") val tipo: TipoIncidencia,
    @SerializedName("description") val descripcion: String?,
    @SerializedName("reportDate") val fechaReporte: String
)
