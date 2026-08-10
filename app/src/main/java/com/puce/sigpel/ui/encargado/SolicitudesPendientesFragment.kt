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
import com.puce.sigpel.data.remote.dto.EstadoPrestamo
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.data.remote.dto.TipoIncidencia
import com.puce.sigpel.databinding.DialogComentarioBinding
import com.puce.sigpel.databinding.FragmentSolicitudesPendientesBinding
import com.puce.sigpel.ui.common.applyBrandColors
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.textOrNull
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.util.UiState

/** Pantalla 3.8 del md, rol ENCARGADO. */
class SolicitudesPendientesFragment : Fragment() {

    private var _binding: FragmentSolicitudesPendientesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PrestamoAdminViewModel by viewModels {
        simpleViewModelFactory {
            PrestamoAdminViewModel(
                sigpelApp.prestamoRepository,
                sigpelApp.incidenciaRepository
            )
        }
    }

    private lateinit var adapter: SolicitudAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSolicitudesPendientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SolicitudAdapter(
            onAprobar = { prestamo -> viewModel.cambiarEstado(prestamo.id, EstadoPrestamo.APROBADO, null) },
            onRechazar = { prestamo -> showRechazarDialog(prestamo) },
            onDevuelto = { prestamo -> viewModel.cambiarEstado(prestamo.id, EstadoPrestamo.DEVUELTO, null) },
            onIncidencia = { prestamo -> showRegistrarIncidenciaDialog(prestamo) }
        )
        binding.recyclerSolicitudes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSolicitudes.adapter = adapter

        binding.swipeRefresh.applyBrandColors()
        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }
        binding.buttonIncidencias.setOnClickListener {
            findNavController().navigate(R.id.gestionIncidenciasFragment)
        }

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
                    binding.textEmpty.text = getString(R.string.solicitudes_empty)
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

    private fun showRechazarDialog(prestamo: PrestamoResponse) {
        val dialogBinding = DialogComentarioBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.solicitudes_rechazar_titulo)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.solicitudes_rechazar) { _, _ ->
                viewModel.cambiarEstado(prestamo.id, EstadoPrestamo.RECHAZADO, dialogBinding.inputComentario.textOrNull())
            }
            .setNegativeButton(R.string.form_cancelar, null)
            .show()
    }

    private fun showRegistrarIncidenciaDialog(prestamo: PrestamoResponse) {
        val dialogBinding = DialogComentarioBinding.inflate(layoutInflater)
        AlertDialog.Builder(requireContext())
            .setTitle("Registrar Incidencia")
            .setView(dialogBinding.root)
            .setPositiveButton("Registrar") { _, _ ->
                val descripcion = dialogBinding.inputComentario.textOrNull()
                viewModel.registrarIncidencia(prestamo.id, TipoIncidencia.DANIO, descripcion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
