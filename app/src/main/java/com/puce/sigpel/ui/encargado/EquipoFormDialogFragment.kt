package com.puce.sigpel.ui.encargado

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.databinding.DialogEquipoFormBinding
import com.puce.sigpel.ui.common.textOrNull

/** Formulario de alta de equipo (pantalla 3.7). El backend no soporta editar equipos existentes,
 * solo crear/cambiar estado/eliminar, asi que este dialogo solo se usa para "Nuevo equipo". */
class EquipoFormDialogFragment : DialogFragment() {

    var categorias: List<CategoriaEquipoResponse> = emptyList()
    var onSubmit: ((categoriaId: Long, nombre: String, descripcion: String?) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogEquipoFormBinding.inflate(layoutInflater)

        var selectedCategoriaId: Long? = categorias.firstOrNull()?.id
        val nombres = categorias.map { it.nombre }
        binding.inputCategoria.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, nombres))
        binding.inputCategoria.setText(categorias.firstOrNull()?.nombre.orEmpty(), false)
        binding.inputCategoria.setOnItemClickListener { _, _, position, _ ->
            selectedCategoriaId = categorias[position].id
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.gestion_equipos_nuevo)
            .setView(binding.root)
            .setPositiveButton(R.string.form_guardar) { _, _ ->
                val nombre = binding.inputNombre.textOrNull()
                val categoriaId = selectedCategoriaId
                when {
                    nombre == null -> Toast.makeText(requireContext(), R.string.form_error_nombre_requerido, Toast.LENGTH_SHORT).show()
                    categoriaId == null -> Toast.makeText(requireContext(), R.string.form_categoria_hint, Toast.LENGTH_SHORT).show()
                    else -> onSubmit?.invoke(categoriaId, nombre, binding.inputDescripcion.textOrNull())
                }
            }
            .setNegativeButton(R.string.form_cancelar, null)
            .create()
    }
}
