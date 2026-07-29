package com.puce.sigpel.ui.encargado

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.data.remote.dto.TipoIncidencia
import com.puce.sigpel.databinding.FragmentNuevaIncidenciaBinding
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.textOrNull
import com.puce.sigpel.ui.common.tipoIncidenciaLabel
import com.puce.sigpel.util.UiState

/** Pantalla 3.9 del md, rol ENCARGADO. */
class NuevaIncidenciaFragment : Fragment() {

    private var _binding: FragmentNuevaIncidenciaBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IncidenciaViewModel by viewModels {
        simpleViewModelFactory { IncidenciaViewModel(sigpelApp.incidenciaRepository, sigpelApp.prestamoRepository) }
    }

    private var prestamosDisponibles: List<PrestamoResponse> = emptyList()
    private var prestamoSeleccionado: PrestamoResponse? = null
    private var tipoSeleccionado: TipoIncidencia? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNuevaIncidenciaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTipoDropdown()

        binding.buttonRegistrar.setOnClickListener {
            val prestamo = prestamoSeleccionado
            val tipo = tipoSeleccionado
            if (prestamo == null || tipo == null) {
                showFeedback(getString(R.string.incidencia_error), isError = true)
                return@setOnClickListener
            }
            viewModel.registrar(prestamo.id, tipo, binding.inputDescripcion.textOrNull())
        }

        viewModel.prestamos.observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                prestamosDisponibles = state.data
                setupPrestamoDropdown(state.data)
            }
        }

        viewModel.registrarState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.buttonRegistrar.isEnabled = false
                    binding.textFeedback.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    binding.buttonRegistrar.isEnabled = true
                    showFeedback(getString(R.string.incidencia_exito), isError = false)
                    binding.inputDescripcion.setText("")
                }
                is UiState.Error -> {
                    binding.progress.setVisible(false)
                    binding.buttonRegistrar.isEnabled = true
                    showFeedback(state.message, isError = true)
                }
                null -> Unit
            }
        }

        viewModel.loadPrestamos()
    }

    private fun setupPrestamoDropdown(prestamos: List<PrestamoResponse>) {
        val labels = prestamos.map { "${it.equipoNombre} — ${it.estudianteUser}" }
        binding.inputPrestamo.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels))
        binding.inputPrestamo.setOnItemClickListener { _, _, position, _ ->
            prestamoSeleccionado = prestamos[position]
        }
    }

    private fun setupTipoDropdown() {
        val tipos = TipoIncidencia.values()
        val labels = tipos.map { getString(tipoIncidenciaLabel(it)) }
        binding.inputTipo.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels))
        binding.inputTipo.setOnItemClickListener { _, _, position, _ ->
            tipoSeleccionado = tipos[position]
        }
    }

    private fun showFeedback(message: String, isError: Boolean) {
        binding.textFeedback.text = message
        binding.textFeedback.setTextColor(
            ContextCompat.getColor(requireContext(), if (isError) R.color.error else R.color.estado_disponible)
        )
        binding.textFeedback.setVisible(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
