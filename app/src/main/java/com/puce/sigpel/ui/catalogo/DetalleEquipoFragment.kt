package com.puce.sigpel.ui.catalogo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puce.sigpel.R
import com.puce.sigpel.data.auth.Role
import com.puce.sigpel.databinding.FragmentDetalleEquipoBinding
import com.puce.sigpel.ui.common.bindEstadoEquipo
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.util.UiState

/** Pantalla 3.3: publica, boton "Solicitar prestamo" solo visible si hay sesion ESTUDIANTE. */
class DetalleEquipoFragment : Fragment() {

    private var _binding: FragmentDetalleEquipoBinding? = null
    private val binding get() = _binding!!

    private val equipoId: Long by lazy { requireArguments().getLong("equipoId") }

    private val viewModel: DetalleEquipoViewModel by viewModels {
        simpleViewModelFactory { DetalleEquipoViewModel(equipoId, sigpelApp.equipoRepository) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDetalleEquipoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isEstudiante = sigpelApp.authRepository.currentRole == Role.ESTUDIANTE
        binding.textLoginRequerido.setVisible(!isEstudiante)

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

                    val equipo = state.data
                    binding.textNombre.text = equipo.nombre
                    binding.textCategoria.text = getString(R.string.detalle_equipo_categoria, equipo.categoriaNombre)
                    binding.textDescripcion.text = equipo.descripcion.orEmpty()
                    binding.textDescripcion.setVisible(!equipo.descripcion.isNullOrBlank())
                    binding.badgeEstado.bindEstadoEquipo(equipo.estado)
                    binding.buttonSolicitar.setVisible(isEstudiante)

                    binding.buttonSolicitar.setOnClickListener {
                        findNavController().navigate(
                            R.id.action_detalleEquipo_to_solicitarPrestamo,
                            bundleOf("equipoId" to equipo.id, "equipoNombre" to equipo.nombre)
                        )
                    }
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

        viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
