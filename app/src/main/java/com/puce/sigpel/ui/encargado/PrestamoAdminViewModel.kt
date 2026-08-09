package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.EstadoPrestamo
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.data.repository.PrestamoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.8 del md, rol ENCARGADO. Usa GET /prestamos (bandeja completa, ver nota en ApiService). */
class PrestamoAdminViewModel(private val prestamoRepository: PrestamoRepository) : ViewModel() {

    private val _state = MutableLiveData<UiState<List<PrestamoResponse>>>()
    val state: LiveData<UiState<List<PrestamoResponse>>> = _state

    private val _actionState = MutableLiveData<UiState<Unit>?>(null)
    val actionState: LiveData<UiState<Unit>?> = _actionState

    fun load() {
        _state.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.listarTodos()
                .onSuccess { lista -> _state.value = UiState.Success(lista.sortedByDescending { it.fechaSolicitud }) }
                .onFailure { _state.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun cambiarEstado(id: Long, estado: EstadoPrestamo, comentario: String?) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.cambiarEstado(id, estado, comentario)
                .onSuccess { _actionState.value = UiState.Success(Unit); load() }
                .onFailure { _actionState.value = UiState.Error(it.toUserMessage()) }
        }
    }

    // Dentro de PrestamoAdminViewModel.kt
    fun registrarEquipo(nombre: String, serial: String) {
        viewModelScope.launch {
            try {
                // repository.postEquipo(EquipoDto(nombre, serial))
                // Aquí manejarías el éxito, por ejemplo, mostrando un Toast o navegando atrás
            } catch (e: Exception) {
                // Manejo de error
            }
        }
    }

    fun marcarComoDevuelto(prestamoId: Long) {
        _actionState.value = UiState.Loading
        viewModelScope.launch {
            try {
                // prestamoRepository.marcarDevuelto(prestamoId)
                _actionState.value = UiState.Success(Unit)
                load() // Recarga la lista para reflejar el cambio
            } catch (e: Exception) {
                _actionState.value = UiState.Error(e.toUserMessage())
            }
        }
    }
}
