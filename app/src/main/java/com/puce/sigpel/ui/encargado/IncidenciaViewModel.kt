package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.IncidenciaResponse
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.data.remote.dto.TipoIncidencia
import com.puce.sigpel.data.repository.IncidenciaRepository
import com.puce.sigpel.data.repository.PrestamoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.9 del md, rol ENCARGADO. */
class IncidenciaViewModel(
    private val incidenciaRepository: IncidenciaRepository,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _prestamos = MutableLiveData<UiState<List<PrestamoResponse>>>()
    val prestamos: LiveData<UiState<List<PrestamoResponse>>> = _prestamos

    private val _registrarState = MutableLiveData<UiState<IncidenciaResponse>>()
    val registrarState: LiveData<UiState<IncidenciaResponse>> = _registrarState

    fun loadPrestamos() {
        _prestamos.value = UiState.Loading
        viewModelScope.launch {
            prestamoRepository.listarTodos()
                .onSuccess { _prestamos.value = UiState.Success(it) }
                .onFailure { _prestamos.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun registrar(prestamoId: Long, tipo: TipoIncidencia, descripcion: String?) {
        _registrarState.value = UiState.Loading
        viewModelScope.launch {
            incidenciaRepository.registrar(prestamoId, tipo, descripcion)
                .onSuccess { _registrarState.value = UiState.Success(it) }
                .onFailure { _registrarState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
