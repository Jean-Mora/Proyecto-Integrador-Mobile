package com.puce.sigpel.util

import retrofit2.HttpException
import java.io.IOException

/** Traduce excepciones de red/HTTP a un mensaje entendible para mostrar en la UI. */
fun Throwable.toUserMessage(): String = when (this) {
    is HttpException -> when (code()) {
        401 -> "Tu sesión expiró, inicia sesión de nuevo"
        403 -> "No tienes permisos para esta acción"
        404 -> "No se encontró el recurso solicitado"
        in 500..599 -> "Error del servidor, intenta más tarde"
        else -> "Ocurrió un error inesperado (código ${code()})"
    }
    is IOException -> "Revisa tu conexión e intenta de nuevo"
    else -> message ?: "Ocurrió un error inesperado"
}
