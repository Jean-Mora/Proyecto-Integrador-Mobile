package com.puce.sigpel.ui.encargado

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.puce.sigpel.R
import com.puce.sigpel.databinding.DialogCategoriaFormBinding
import com.puce.sigpel.ui.common.textOrNull

/** Crear o editar una categoria (pantalla 3.7, seccion categorias). */
class CategoriaFormDialogFragment : DialogFragment() {

    var nombreInicial: String? = null
    var onSubmit: ((nombre: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogCategoriaFormBinding.inflate(layoutInflater)
        binding.inputNombre.setText(nombreInicial.orEmpty())

        val titleRes = if (nombreInicial == null) R.string.gestion_categorias_nueva else R.string.gestion_categorias_editar

        return AlertDialog.Builder(requireContext())
            .setTitle(titleRes)
            .setView(binding.root)
            .setPositiveButton(R.string.form_guardar) { _, _ ->
                val nombre = binding.inputNombre.textOrNull()
                if (nombre == null) {
                    Toast.makeText(requireContext(), R.string.form_error_nombre_requerido, Toast.LENGTH_SHORT).show()
                } else {
                    onSubmit?.invoke(nombre)
                }
            }
            .setNegativeButton(R.string.form_cancelar, null)
            .create()
    }
}
