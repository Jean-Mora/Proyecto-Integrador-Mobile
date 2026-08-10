package com.puce.sigpel.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Espejo de com.puce.sigpel.dto.EquipmentDtos en el backend (EquipmentController,
 * @RequestMapping("/equipment")). El backend usa nombres en ingles; se mantienen los
 * nombres en espanol en Kotlin (usados en toda la UI) y se mapea el JSON con @SerializedName.
 */
enum class EstadoEquipo {
    @SerializedName("AVAILABLE") DISPONIBLE,
    @SerializedName("LOANED") PRESTADO,
    @SerializedName("MAINTENANCE") MANTENIMIENTO
}

data class EquipoRequest(
    @SerializedName("categoryId") val categoriaId: Long,
    @SerializedName("name") val nombre: String,
    @SerializedName("serialNumber") val numeroSerie: String,
    @SerializedName("description") val descripcion: String? = null
)

data class EquipoEstadoRequest(
    @SerializedName("status") val estado: EstadoEquipo
)

data class EquipoResponse(
    val id: Long,
    @SerializedName("categoryId") val categoriaId: Long,
    @SerializedName("categoryName") val categoriaNombre: String,
    @SerializedName("name") val nombre: String,
    @SerializedName("serialNumber") val numeroSerie: String?,
    @SerializedName("status") val estado: EstadoEquipo,
    @SerializedName("description") val descripcion: String?,
    @SerializedName("imageUrl") val imagenUrl: String? = null
)
