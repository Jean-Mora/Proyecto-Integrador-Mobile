package com.puce.sigpel.ui.catalogo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.repository.EquipoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.3 del md. */
class DetalleEquipoViewModel(
    private val equipoId: Long,
    private val equipoRepository: EquipoRepository
) : ViewModel() {

    private val _state = MutableLiveData<UiState<EquipoResponse>>()
    val state: LiveData<UiState<EquipoResponse>> = _state

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            equipoRepository.obtener(equipoId)
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }
}

