package com.puce.sigpel.data.remote.dto

/** Espejo de com.puce.sigpel.dto.EquipoDtos en el backend. */
enum class EstadoEquipo { DISPONIBLE, PRESTADO, MANTENIMIENTO }

data class EquipoRequest(
    val categoriaId: Long,
    val nombre: String,
    val descripcion: String? = null
)

data class EquipoEstadoRequest(
    val estado: EstadoEquipo
)

data class EquipoResponse(
    val id: Long,
    val categoriaId: Long,
    val categoriaNombre: String,
    val nombre: String,
    val estado: EstadoEquipo,
    val descripcion: String?
)
