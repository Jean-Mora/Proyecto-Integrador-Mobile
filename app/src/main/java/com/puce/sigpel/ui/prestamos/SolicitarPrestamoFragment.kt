package com.puce.sigpel.ui.prestamos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.puce.sigpel.R
import com.puce.sigpel.databinding.FragmentSolicitarPrestamoBinding
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.util.DateFormat
import com.puce.sigpel.util.UiState

/** Pantalla 3.4 del md: solo ESTUDIANTE (protegido por rol en DetalleEquipoFragment).
 * "Mis prestamos" (ver el listado propio) no es parte de HU-20, asi que al confirmar
 * se muestra el mensaje de exito y se vuelve al catalogo. */
class SolicitarPrestamoFragment : Fragment() {

    private var _binding: FragmentSolicitarPrestamoBinding? = null
    private val binding get() = _binding!!

    private val equipoId: Long by lazy { requireArguments().getLong("equipoId") }
    private val equipoNombre: String by lazy { requireArguments().getString("equipoNombre").orEmpty() }

    private var fechaSeleccionadaIso: String? = null

    private val viewModel: SolicitarPrestamoViewModel by viewModels {
        simpleViewModelFactory { SolicitarPrestamoViewModel(equipoId, sigpelApp.prestamoRepository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSolicitarPrestamoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputEquipo.setText(equipoNombre)
        binding.inputEquipo.isEnabled = false

        binding.layoutFecha.setEndIconOnClickListener { showDatePicker() }
        binding.inputFecha.setOnClickListener { showDatePicker() }

        binding.buttonConfirmar.setOnClickListener {
            viewModel.solicitar(fechaSeleccionadaIso)
        }

        viewModel.solicitarState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.buttonConfirmar.isEnabled = false
                    binding.textFeedback.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    binding.buttonConfirmar.isEnabled = false
                    binding.textFeedback.text = getString(R.string.solicitar_prestamo_exito)
                    binding.textFeedback.setTextColor(ContextCompat.getColor(requireContext(), R.color.estado_disponible))
                    binding.textFeedback.setVisible(true)
                    binding.root.postDelayed({
                        findNavController().popBackStack(R.id.catalogoFragment, false)
                    }, 1200)
                }
                is UiState.Error -> {
                    binding.progress.setVisible(false)
                    binding.buttonConfirmar.isEnabled = true
                    binding.textFeedback.text = state.message
                    binding.textFeedback.setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
                    binding.textFeedback.setVisible(true)
                }
                null -> Unit
            }
        }
    }

    private fun showDatePicker() {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.solicitar_prestamo_fecha)
            .build()
        picker.addOnPositiveButtonClickListener { epochMillis ->
            fechaSeleccionadaIso = DateFormat.epochMillisToIso(epochMillis)
            binding.inputFecha.setText(DateFormat.formatDate(fechaSeleccionadaIso))
        }
        picker.show(childFragmentManager, "fecha_devolucion")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
