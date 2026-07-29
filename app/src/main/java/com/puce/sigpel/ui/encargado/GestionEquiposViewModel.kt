package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.data.remote.dto.EquipoRequest
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import com.puce.sigpel.data.repository.CategoriaRepository
import com.puce.sigpel.data.repository.EquipoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/**
 * Pantalla 3.7 del md, rol ENCARGADO. El backend (EquipoController) solo permite
 * crear, cambiar el estado (PATCH solo admite "estado") y eliminar; no hay edicion
 * completa de nombre/categoria/descripcion para un equipo existente.
 */
class GestionEquiposViewModel(
    private val equipoRepository: EquipoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _equiposState = MutableLiveData<UiState<List<EquipoResponse>>>()
    val equiposState: LiveData<UiState<List<EquipoResponse>>> = _equiposState

    private val _categorias = MutableLiveData<List<CategoriaEquipoResponse>>(emptyList())
    val categorias: LiveData<List<CategoriaEquipoResponse>> = _categorias

    private val _actionState = MutableLiveData<UiState<Unit>?>(null)
    val actionState: LiveData<UiState<Unit>?> = _actionState

    fun load() {
        _equiposState.value = UiState.Loading
        viewModelScope.launch {
            categoriaRepository.listar().onSuccess { _categorias.value = it }
            equipoRepository.listar()
                .onSuccess { _equiposState.value = UiState.Success(it) }
                .onFailure { _equiposState.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun crear(categoriaId: Long, nombre: String, descripcion: String?) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            equipoRepository.crear(EquipoRequest(categoriaId, nombre, descripcion))
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
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

    fun consumeActionState() {
        _actionState.value = null
    }
}
