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

/** Pantalla 3.4 del md. Nota: PrestamoRequest del backend solo acepta equipoId y
 * fechaDevolucionEstimada (ver Backend/.../dto/PrestamoDtos.kt); el comentario
 * solo existe al cambiar de estado (PATCH), no al solicitar. */
class SolicitarPrestamoViewModel(
    private val equipoId: Long,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _solicitarState = MutableLiveData<UiState<PrestamoResponse>>()
    val solicitarState: LiveData<UiState<PrestamoResponse>> = _solicitarState

    fun solicitar(fechaDevolucionEstimadaIso: String?) {
        _solicitarState.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.solicitar(equipoId, fechaDevolucionEstimadaIso)
                .onSuccess { _solicitarState.value = UiState.Success(it) }
                .onFailure { _solicitarState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
