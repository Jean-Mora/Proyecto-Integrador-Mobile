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

/** Pantalla 3.5 del md (HU-29): lista de prestamos del ESTUDIANTE via GET /loans/me. */
class MisPrestamosViewModel(private val prestamoRepository: PrestamoRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<PrestamoResponse>>>()
    val state: LiveData<UiState<List<PrestamoResponse>>> = _state

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.misPrestamos()
                .onSuccess { lista -> _state.value = UiState.Success(lista.sortedByDescending { it.fechaSolicitud }) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
