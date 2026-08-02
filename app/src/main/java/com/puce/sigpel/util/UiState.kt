package com.puce.sigpel.util

/** Estado generico para pantallas alimentadas por un solo request (loading/success/error). */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
