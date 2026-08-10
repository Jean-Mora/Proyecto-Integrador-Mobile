package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import com.puce.sigpel.data.repository.EquipoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.7 del md (equipos), rol ENCARGADO: listar, cambiar estado y eliminar. */
class GestionEquiposViewModel(private val equipoRepository: EquipoRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<EquipoResponse>>>()
    val state: LiveData<UiState<List<EquipoResponse>>> = _state

    private val _actionState = MutableLiveData<UiState<Unit>?>(null)
    val actionState: LiveData<UiState<Unit>?> = _actionState

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            equipoRepository.listar()
                .onSuccess { lista -> _state.value = UiState.Success(lista.sortedBy { it.nombre }) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun cambiarEstado(id: Long, estado: EstadoEquipo) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            equipoRepository.actualizarEstado(id, estado)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun eliminar(id: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            equipoRepository.eliminar(id)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
