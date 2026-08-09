package com.puce.sigpel.ui.catalogo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.repository.CategoriaRepository
import com.puce.sigpel.data.repository.EquipoRepository
import com.puce.sigpel.util.UiState
import com.puce.sigpel.util.toUserMessage
import kotlinx.coroutines.launch

/** Pantalla 3.1 del md: catalogo publico con buscador y filtro por categoria. */
class CatalogoViewModel(
    private val equipoRepository: EquipoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _categorias = MutableLiveData<List<CategoriaEquipoResponse>>(emptyList())
    val categorias: LiveData<List<CategoriaEquipoResponse>> = _categorias

    private val _equiposState = MutableLiveData<UiState<List<EquipoResponse>>>()
    val equiposState: LiveData<UiState<List<EquipoResponse>>> = _equiposState

    private var allEquipos: List<EquipoResponse> = emptyList()
    private var searchQuery: String = ""
    private var selectedCategoriaId: Long? = null

    fun load() {
        _equiposState.value = UiState.Loading
        viewModelScope.launch {
            categoriaRepository.listar().onSuccess { _categorias.value = it }

            equipoRepository.listar()
                .onSuccess {
                    allEquipos = it
                    applyFilter()
                }
                .onFailure { _equiposState.value = UiState.Error(it.toUserMessage()) }
        }
    }

    fun onSearchChanged(query: String) {
        searchQuery = query
        applyFilter()
    }

    fun onCategoriaSelected(categoriaId: Long?) {
        selectedCategoriaId = categoriaId
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = allEquipos.filter { equipo ->
            val matchesQuery = searchQuery.isBlank() || equipo.nombre.contains(searchQuery, ignoreCase = true)
            val matchesCategoria = selectedCategoriaId == null || equipo.categoriaId == selectedCategoriaId
            matchesQuery && matchesCategoria
        }
        _equiposState.value = UiState.Success(filtered)
    }
}

