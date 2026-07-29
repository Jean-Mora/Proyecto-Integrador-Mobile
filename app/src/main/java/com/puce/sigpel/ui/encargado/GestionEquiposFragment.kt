package com.puce.sigpel.ui.encargado

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import com.puce.sigpel.databinding.FragmentGestionEquiposBinding
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.util.UiState

/** Pantalla 3.7 del md, rol ENCARGADO. */
class GestionEquiposFragment : Fragment() {

    private var _binding: FragmentGestionEquiposBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GestionEquiposViewModel by viewModels {
        simpleViewModelFactory { GestionEquiposViewModel(sigpelApp.equipoRepository, sigpelApp.categoriaRepository) }
    }

    private lateinit var adapter: EquipoAdminAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGestionEquiposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EquipoAdminAdapter(
            onCambiarEstado = { equipo -> showCambiarEstadoDialog(equipo) },
            onEliminar = { equipo -> showEliminarDialog(equipo) }
        )
        val spanCount = resources.getInteger(R.integer.grid_columns_admin)
        binding.recyclerEquipos.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recyclerEquipos.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }
        binding.buttonCategorias.setOnClickListener {
            findNavController().navigate(R.id.action_gestionEquipos_to_gestionCategorias)
        }
        binding.fabAgregar.setOnClickListener { showNuevoEquipoDialog() }

        viewModel.equiposState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.textEmpty.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    adapter.submitList(state.data)
                    binding.textEmpty.text = getString(R.string.gestion_equipos_empty)
                    binding.textEmpty.setVisible(state.data.isEmpty())
                }
                is UiState.Error -> {
                    binding.progress.setVisible(false)
                    adapter.submitList(emptyList())
                    binding.textEmpty.text = state.message
                    binding.textEmpty.setVisible(true)
                }
                null -> Unit
            }
        }

        viewModel.actionState.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Error) toast(state.message)
        }

        viewModel.load()
    }

    private fun showNuevoEquipoDialog() {
        val dialog = EquipoFormDialogFragment().apply {
            categorias = viewModel.categorias.value.orEmpty()
            onSubmit = { categoriaId, nombre, descripcion -> viewModel.crear(categoriaId, nombre, descripcion) }
        }
        dialog.show(childFragmentManager, "nuevo_equipo")
    }

    private fun showCambiarEstadoDialog(equipo: EquipoResponse) {
        val estados = EstadoEquipo.values()
        val labels = estados.map {
            when (it) {
                EstadoEquipo.DISPONIBLE -> getString(R.string.estado_disponible)
                EstadoEquipo.PRESTADO -> getString(R.string.estado_prestado)
                EstadoEquipo.MANTENIMIENTO -> getString(R.string.estado_mantenimiento)
            }
        }.toTypedArray()
        val checkedItem = estados.indexOf(equipo.estado)

        AlertDialog.Builder(requireContext())
            .setTitle(equipo.nombre)
            .setSingleChoiceItems(labels, checkedItem) { dialog, which ->
                viewModel.cambiarEstado(equipo.id, estados[which])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.form_cancelar, null)
            .show()
    }

    private fun showEliminarDialog(equipo: EquipoResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(equipo.nombre)
            .setMessage(R.string.gestion_equipos_eliminar_confirmar)
            .setPositiveButton(R.string.si) { _, _ -> viewModel.eliminar(equipo.id) }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
