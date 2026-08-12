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
import com.puce.sigpel.data.remote.dto.IncidenciaResponse
import com.puce.sigpel.databinding.FragmentGestionIncidenciasBinding
import com.puce.sigpel.ui.common.applyBrandColors
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.util.UiState

/** Pantalla de gestion de incidencias (encargado): listar y eliminar. */
class GestionIncidenciasFragment : Fragment() {

    private var _binding: FragmentGestionIncidenciasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GestionIncidenciasViewModel by viewModels {
        simpleViewModelFactory { GestionIncidenciasViewModel(sigpelApp.incidenciaRepository) }
    }

    private lateinit var adapter: IncidenciaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGestionIncidenciasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncidenciaAdapter(onEliminar = { incidencia -> showEliminarDialog(incidencia) })
        binding.recyclerIncidencias.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidencias.adapter = adapter

        binding.swipeRefresh.applyBrandColors()
        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

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
                    binding.textEmpty.text = getString(R.string.gestion_incidencias_empty)
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

    private fun showEliminarDialog(incidencia: IncidenciaResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.gestion_incidencias_prestamo, incidencia.prestamoId))
            .setMessage(R.string.gestion_incidencias_eliminar_confirmar)
            .setPositiveButton(R.string.si) { _, _ -> viewModel.eliminar(incidencia.id) }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
