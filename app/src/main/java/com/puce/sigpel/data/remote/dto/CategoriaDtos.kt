package com.puce.sigpel.data.remote.dto

import com.google.gson.annotations.SerializedName

/** Espejo de com.puce.sigpel.dto.EquipmentCategoryDtos en el backend (@RequestMapping("/categories")). */
data class CategoriaEquipoRequest(
    @SerializedName("name") val nombre: String
)

data class CategoriaEquipoResponse(
    val id: Long,
    @SerializedName("name") val nombre: String
)
