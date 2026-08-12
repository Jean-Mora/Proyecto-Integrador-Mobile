package com.puce.sigpel.ui.encargado

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.databinding.FragmentGestionCategoriasBinding
import com.puce.sigpel.ui.common.applyBrandColors
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.util.UiState

/** Pantalla de gestion de categorias (encargado): crear, editar y eliminar. */
class GestionCategoriasFragment : Fragment() {

    private var _binding: FragmentGestionCategoriasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GestionCategoriasViewModel by viewModels {
        simpleViewModelFactory { GestionCategoriasViewModel(sigpelApp.categoriaRepository) }
    }

    private lateinit var adapter: CategoriaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGestionCategoriasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CategoriaAdapter(
            onEditar = { categoria -> showFormDialog(categoria) },
            onEliminar = { categoria -> showEliminarDialog(categoria) }
        )
        binding.recyclerCategorias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCategorias.adapter = adapter

        binding.swipeRefresh.applyBrandColors()
        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }
        binding.fabAgregar.setOnClickListener { showFormDialog(null) }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.layoutEmpty.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    adapter.submitList(state.data)
                    binding.textEmpty.text = getString(R.string.gestion_categorias_empty)
                    binding.layoutEmpty.setVisible(state.data.isEmpty())
                }
                is UiState.Error -> {
                    binding.progress.setVisible(false)
                    adapter.submitList(emptyList())
                    binding.textEmpty.text = state.message
                    binding.layoutEmpty.setVisible(true)
                }
                null -> Unit
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Error) toast(state.message)
        }

        viewModel.load()
    }

    private fun showFormDialog(existing: CategoriaEquipoResponse?) {
        val dialog = CategoriaFormDialogFragment().apply {
            nombreInicial = existing?.nombre
            onSubmit = { nombre ->
                if (existing == null) viewModel.crear(nombre) else viewModel.editar(existing.id, nombre)
            }
        }
        dialog.show(childFragmentManager, "form_categoria")
    }

    private fun showEliminarDialog(categoria: CategoriaEquipoResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(categoria.nombre)
            .setMessage(R.string.gestion_categorias_eliminar_confirmar)
            .setPositiveButton(R.string.si) { _, _ -> viewModel.eliminar(categoria.id) }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
