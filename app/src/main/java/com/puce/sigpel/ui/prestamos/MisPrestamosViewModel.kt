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

/** Pantalla 3.5 del md. */
class MisPrestamosViewModel(private val prestamoRepository: PrestamoRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<PrestamoResponse>>>()
    val state: LiveData<UiState<List<PrestamoResponse>>> = _state

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.misPrestamos()
                .onSuccess { _state.value = UiState.Success(it.sortedByDescending { p -> p.fechaSolicitud }) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
