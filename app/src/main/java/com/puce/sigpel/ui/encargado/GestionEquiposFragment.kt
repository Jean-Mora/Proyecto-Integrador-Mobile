package com.puce.sigpel.ui.encargado

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.data.remote.dto.EstadoEquipo
import com.puce.sigpel.databinding.FragmentGestionEquiposBinding
import com.puce.sigpel.ui.common.applyBrandColors
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.util.UiState

/** Pantalla de gestion de equipos (encargado): listar, cambiar estado y eliminar. */
class GestionEquiposFragment : Fragment() {

    private var _binding: FragmentGestionEquiposBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GestionEquiposViewModel by viewModels {
        simpleViewModelFactory { GestionEquiposViewModel(sigpelApp.equipoRepository) }
    }

    private lateinit var adapter: EquipoAdminAdapter

    private val estadosOrdenados = listOf(EstadoEquipo.DISPONIBLE, EstadoEquipo.PRESTADO, EstadoEquipo.MANTENIMIENTO)

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
        binding.recyclerEquipos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEquipos.adapter = adapter

        binding.swipeRefresh.applyBrandColors()
        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }
        binding.fabAgregar.setOnClickListener { findNavController().navigate(R.id.registrarEquipoFragment) }

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
                    binding.textEmpty.text = getString(R.string.gestion_equipos_empty)
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
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    private fun showCambiarEstadoDialog(equipo: EquipoResponse) {
        val labels = estadosOrdenados.map { getString(estadoLabelRes(it)) }.toTypedArray()
        var seleccion = estadosOrdenados.indexOf(equipo.estado).coerceAtLeast(0)
        AlertDialog.Builder(requireContext())
            .setTitle(equipo.nombre)
            .setSingleChoiceItems(labels, seleccion) { _, which -> seleccion = which }
            .setPositiveButton(R.string.form_guardar) { _, _ ->
                viewModel.cambiarEstado(equipo.id, estadosOrdenados[seleccion])
            }
            .setNegativeButton(R.string.form_cancelar, null)
            .show()
    }

    private fun estadoLabelRes(estado: EstadoEquipo) = when (estado) {
        EstadoEquipo.DISPONIBLE -> R.string.estado_disponible
        EstadoEquipo.PRESTADO -> R.string.estado_prestado
        EstadoEquipo.MANTENIMIENTO -> R.string.estado_mantenimiento
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
