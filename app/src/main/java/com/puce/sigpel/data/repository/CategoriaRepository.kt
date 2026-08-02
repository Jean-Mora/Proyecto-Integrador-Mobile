package com.puce.sigpel.data.repository

import com.puce.sigpel.data.remote.ApiService
import com.puce.sigpel.data.remote.dto.CategoriaEquipoRequest
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse

/** Filtro del catalogo (publico) y gestion de categorias (pantalla 3.7, rol ENCARGADO). */
class CategoriaRepository(private val apiService: ApiService) {

    suspend fun listar(): Result<List<CategoriaEquipoResponse>> = runCatching {
        apiService.listarCategorias()
    }

    suspend fun crear(nombre: String): Result<CategoriaEquipoResponse> = runCatching {
        apiService.crearCategoria(CategoriaEquipoRequest(nombre))
    }

    suspend fun editar(id: Long, nombre: String): Result<CategoriaEquipoResponse> = runCatching {
        apiService.editarCategoria(id, CategoriaEquipoRequest(nombre))
    }

    suspend fun eliminar(id: Long): Result<Unit> = runCatching {
        apiService.eliminarCategoria(id)
        Unit
    }
}
