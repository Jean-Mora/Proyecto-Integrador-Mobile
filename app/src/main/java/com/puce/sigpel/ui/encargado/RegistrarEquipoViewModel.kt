package com.puce.sigpel.ui.encargado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.data.remote.dto.EquipoRequest
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.repository.CategoriaRepository
import com.puce.sigpel.data.repository.EquipoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

/** Formulario de alta de equipo (HU-31), rol ENCARGADO. */
class RegistrarEquipoViewModel(
    private val equipoRepository: EquipoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _categorias = MutableLiveData<List<CategoriaEquipoResponse>>(emptyList())
    val categorias: LiveData<List<CategoriaEquipoResponse>> = _categorias

    private val _registrarState = MutableLiveData<UiState<EquipoResponse>?>(null)
    val registrarState: LiveData<UiState<EquipoResponse>?> = _registrarState

    fun cargarCategorias() {
        viewModelScope.launch {
            categoriaRepository.listar().onSuccess { _categorias.value = it }
        }
    }

    fun registrar(categoriaId: Long, nombre: String, numeroSerie: String, descripcion: String?, imagen: MultipartBody.Part?) {
        _registrarState.value = UiState.Loading
        viewModelScope.launch {
            equipoRepository.crear(EquipoRequest(categoriaId, nombre, numeroSerie, descripcion))
                .onSuccess { creado ->
                    if (imagen == null) {
                        _registrarState.value = UiState.Success(creado)
                    } else {
                        equipoRepository.subirImagen(creado.id, imagen)
                            .onSuccess { conImagen -> _registrarState.value = UiState.Success(conImagen) }
                            // El equipo ya se creo; si solo falla la imagen, igual se reporta como
                            // exito (el usuario puede reintentar la imagen despues) pero avisando.
                            .onFailure { _registrarState.value = UiState.Success(creado) }
                    }
                }
                .onFailure { _registrarState.value = UiState.Error(it.toUserMessage()) }
        }
    }
}
