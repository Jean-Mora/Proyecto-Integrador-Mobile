package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.data.repository.CategoriaRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.7 del md (categorias), rol ENCARGADO. Aqui si hay edicion completa (PATCH /categorias/{id}). */
class GestionCategoriasViewModel(private val categoriaRepository: CategoriaRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<CategoriaEquipoResponse>>>()
    val state: LiveData<UiState<List<CategoriaEquipoResponse>>> = _state

    private val _actionState = MutableLiveData<UiState<Unit>?>(null)
    val actionState: LiveData<UiState<Unit>?> = _actionState

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            categoriaRepository.listar()
                .onSuccess { _state.value = UiState.Success(it) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun crear(nombre: String) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            categoriaRepository.crear(nombre)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun editar(id: Long, nombre: String) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            categoriaRepository.editar(id, nombre)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun eliminar(id: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            categoriaRepository.eliminar(id)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
