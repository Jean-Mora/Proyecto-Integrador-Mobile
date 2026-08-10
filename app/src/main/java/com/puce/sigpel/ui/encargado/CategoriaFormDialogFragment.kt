package com.puce.sigpel.ui.encargado

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.puce.sigpel.R
import com.puce.sigpel.databinding.DialogCategoriaFormBinding
import com.puce.sigpel.ui.common.textOrNull

/** Dialogo compartido para crear/editar una categoria (HU gestion de categorias, rol ENCARGADO). */
class CategoriaFormDialogFragment : DialogFragment() {

    var nombreInicial: String? = null
    var onSubmit: ((nombre: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogCategoriaFormBinding.inflate(layoutInflater)
        binding.inputNombre.setText(nombreInicial)

        val titulo = if (nombreInicial == null) R.string.gestion_categorias_nueva else R.string.gestion_categorias_editar

        return AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setView(binding.root)
            .setPositiveButton(R.string.form_guardar) { _, _ ->
                binding.inputNombre.textOrNull()?.let { onSubmit?.invoke(it) }
            }
            .setNegativeButton(R.string.form_cancelar, null)
            .create()
    }
}
