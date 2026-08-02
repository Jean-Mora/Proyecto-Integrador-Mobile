package com.puce.sigpel.data.remote.dto

/** Espejo de com.puce.sigpel.dto.CategoriaEquipoDtos en el backend. */
data class CategoriaEquipoRequest(
    val nombre: String
)

data class CategoriaEquipoResponse(
    val id: Long,
    val nombre: String
)
