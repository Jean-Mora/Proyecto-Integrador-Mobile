package com.puce.sigpel.ui.prestamos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puce.sigpel.R
import com.puce.sigpel.databinding.FragmentMisPrestamosBinding
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.util.UiState

/** Pantalla 3.5 del md: solo ESTUDIANTE. */
class MisPrestamosFragment : Fragment() {

    private var _binding: FragmentMisPrestamosBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MisPrestamosViewModel by viewModels {
        simpleViewModelFactory { MisPrestamosViewModel(sigpelApp.prestamoRepository) }
    }

    private lateinit var adapter: PrestamoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMisPrestamosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PrestamoAdapter { prestamo ->
            findNavController().navigate(
                R.id.action_misPrestamos_to_detallePrestamo,
                bundleOf("prestamoId" to prestamo.id)
            )
        }
        binding.recyclerPrestamos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPrestamos.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.textEmpty.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    adapter.submitList(state.data)
                    binding.textEmpty.text = getString(R.string.mis_prestamos_empty)
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

        viewModel.load()
    }

    override fun onResume() {
        super.onResume()
        // Refresca al volver de Detalle/Solicitar (puede haber cambiado el estado o haberse cancelado).
        if (view != null) viewModel.load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
