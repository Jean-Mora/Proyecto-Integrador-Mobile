package com.puce.sigpel.ui.prestamos

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.EstadoPrestamo
import com.puce.sigpel.databinding.FragmentDetallePrestamoBinding
import com.puce.sigpel.ui.common.bindEstadoPrestamo
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.ui.common.toast
import com.puce.sigpel.util.DateFormat
import com.puce.sigpel.util.UiState

/** Pantalla 3.6: detalle + cancelar (solo si estado == PENDIENTE y el dueno es quien pide, validado por el backend). */
class DetallePrestamoFragment : Fragment() {

    private var _binding: FragmentDetallePrestamoBinding? = null
    private val binding get() = _binding!!

    private val prestamoId: Long by lazy { requireArguments().getLong("prestamoId") }

    private val viewModel: DetallePrestamoViewModel by viewModels {
        simpleViewModelFactory { DetallePrestamoViewModel(prestamoId, sigpelApp.prestamoRepository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetallePrestamoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.contentGroup.setVisible(false)
                    binding.textError.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    binding.textError.setVisible(false)
                    binding.contentGroup.setVisible(true)
                    bindPrestamo(state.data)
                }
                is UiState.Error -> {
                    binding.progress.setVisible(false)
                    binding.contentGroup.setVisible(false)
                    binding.textError.text = state.message
                    binding.textError.setVisible(true)
                }
                null -> Unit
            }
        }

        viewModel.cancelState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> binding.buttonCancelar.isEnabled = false
                is UiState.Success -> {
                    toast(getString(R.string.detalle_prestamo_cancelada))
                    findNavController().popBackStack()
                }
                is UiState.Error -> {
                    binding.buttonCancelar.isEnabled = true
                    toast(state.message)
                }
                null -> Unit
            }
        }

        viewModel.load()
    }

    private fun bindPrestamo(prestamo: com.puce.sigpel.data.remote.dto.PrestamoResponse) {
        binding.textEquipo.text = prestamo.equipoNombre
        binding.badgeEstado.bindEstadoPrestamo(prestamo.estado)
        binding.textFechaSolicitud.text =
            getString(R.string.detalle_prestamo_fecha_solicitud, DateFormat.formatDateTime(prestamo.fechaSolicitud))
        binding.textFechaDevolucionEstimada.text = getString(
            R.string.detalle_prestamo_fecha_devolucion_estimada,
            DateFormat.formatDate(prestamo.fechaDevolucionEstimada)
        )

        binding.textFechaDevolucionReal.setVisible(prestamo.fechaDevolucionReal != null)
        prestamo.fechaDevolucionReal?.let {
            binding.textFechaDevolucionReal.text =
                getString(R.string.detalle_prestamo_fecha_devolucion_real, DateFormat.formatDate(it))
        }

        binding.textComentario.setVisible(!prestamo.comentario.isNullOrBlank())
        prestamo.comentario?.let {
            binding.textComentario.text = getString(R.string.detalle_prestamo_comentario, it)
        }

        val puedeCancelar = prestamo.estado == EstadoPrestamo.PENDIENTE
        binding.buttonCancelar.setVisible(puedeCancelar)
        binding.buttonCancelar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.detalle_prestamo_cancelar_confirmar_titulo)
                .setMessage(R.string.detalle_prestamo_cancelar_confirmar_msg)
                .setPositiveButton(R.string.si) { _, _ -> viewModel.cancelar() }
                .setNegativeButton(R.string.no, null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
