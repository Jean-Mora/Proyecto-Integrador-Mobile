package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.IncidenciaResponse
import com.puce.sigpel.data.repository.IncidenciaRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.9 del md (gestion de incidencias), rol ENCARGADO: listar y eliminar. */
class GestionIncidenciasViewModel(private val incidenciaRepository: IncidenciaRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<IncidenciaResponse>>>()
    val state: LiveData<UiState<List<IncidenciaResponse>>> = _state

    private val _actionState = MutableLiveData<UiState<Unit>?>(null)
    val actionState: LiveData<UiState<Unit>?> = _actionState

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            incidenciaRepository.listar()
                .onSuccess { lista -> _state.value = UiState.Success(lista) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun eliminar(id: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            incidenciaRepository.eliminar(id)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
