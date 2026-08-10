package com.puce.sigpel.ui.prestamos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.data.repository.PrestamoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/**
 * Pantalla 3.6 del md (HU-30): detalle + cancelar. El backend no expone GET /loans/{id},
 * asi que el detalle se resuelve filtrando la lista de GET /loans/me por id.
 */
class DetallePrestamoViewModel(
    private val prestamoId: Long,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _state = MutableLiveData<UiState<PrestamoResponse>>()
    val state: LiveData<UiState<PrestamoResponse>> = _state

    private val _cancelState = MutableLiveData<UiState<Unit>?>(null)
    val cancelState: LiveData<UiState<Unit>?> = _cancelState

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.misPrestamos()
                .onSuccess { lista ->
                    val prestamo = lista.find { it.id == prestamoId }
                    _state.value = if (prestamo != null) {
                        UiState.Success(prestamo)
                    } else {
                        UiState.Error("No se encontró el préstamo")
                    }
                }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun cancelar() {
        _cancelState.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.cancelar(prestamoId)
                .onSuccess { _cancelState.value = UiState.Success(Unit) }
                .onFailure { _cancelState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
