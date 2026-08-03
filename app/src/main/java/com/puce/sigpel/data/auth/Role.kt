package com.puce.sigpel.data.auth

/** Roles del dominio (ver docs/sigpel_pantallas_moviles.md #1). VISITANTE = sin sesion. */
enum class Role {
    VISITANTE,
    ESTUDIANTE,
    ENCARGADO;

    companion object {
        fun fromCognitoGroups(groups: List<String>?): Role = when {
            groups == null -> VISITANTE
            groups.contains("ENCARGADO") -> ENCARGADO
            groups.contains("ESTUDIANTE") -> ESTUDIANTE
            else -> VISITANTE
        }
    }
}
