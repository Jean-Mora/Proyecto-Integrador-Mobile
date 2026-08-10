package com.puce.sigpel.ui.catalogo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.chip.Chip
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.databinding.FragmentCatalogoBinding
import com.puce.sigpel.ui.common.applyBrandColors
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.ui.common.sigpelApp
import com.puce.sigpel.ui.common.simpleViewModelFactory
import com.puce.sigpel.util.UiState

/** Pantalla 3.1: catalogo publico. VISITANTE y ESTUDIANTE navegan aqui igual. */
class CatalogoFragment : Fragment() {

    private var _binding: FragmentCatalogoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CatalogoViewModel by viewModels {
        simpleViewModelFactory { CatalogoViewModel(sigpelApp.equipoRepository, sigpelApp.categoriaRepository) }
    }

    private lateinit var adapter: EquipoAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCatalogoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EquipoAdapter { equipo ->
            findNavController().navigate(
                R.id.action_catalogo_to_detalleEquipo,
                bundleOf("equipoId" to equipo.id)
            )
        }
        val spanCount = resources.getInteger(R.integer.grid_columns_catalogo)
        binding.recyclerEquipos.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.recyclerEquipos.adapter = adapter

        binding.inputSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                viewModel.onSearchChanged(s?.toString().orEmpty())
            }
        })

        binding.swipeRefresh.applyBrandColors()
        binding.swipeRefresh.setOnRefreshListener { viewModel.load() }

        viewModel.categorias.observe(viewLifecycleOwner) { categorias -> buildChips(categorias) }

        viewModel.equiposState.observe(viewLifecycleOwner) { state ->
            binding.swipeRefresh.isRefreshing = false
            when (state) {
                is UiState.Loading -> {
                    binding.progress.setVisible(true)
                    binding.layoutEmpty.setVisible(false)
                }
                is UiState.Success -> {
                    binding.progress.setVisible(false)
                    adapter.submitList(state.data)
                    binding.textEmpty.text = getString(R.string.catalogo_empty)
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

        if (viewModel.equiposState.value == null) {
            viewModel.load()
        }
    }

    private fun buildChips(categorias: List<CategoriaEquipoResponse>) {
        // categorias.value arranca en emptyList() y el observer dispara con ese valor inicial
        // antes de que termine la carga real; sin el chequeo de isEmpty() esa primera pasada
        // (que solo agrega el chip "Todas") bloquearia para siempre la reconstruccion con las
        // categorias reales por el guard de childCount.
        if (categorias.isEmpty() || binding.chipGroupCategorias.childCount > 0) return
        val chipTodas = Chip(requireContext()).apply {
            text = getString(R.string.catalogo_filtro_todas)
            isCheckable = true
            isChecked = true
        }
        binding.chipGroupCategorias.addView(chipTodas)

        categorias.forEach { categoria ->
            val chip = Chip(requireContext()).apply {
                text = categoria.nombre
                isCheckable = true
                tag = categoria.id
            }
            binding.chipGroupCategorias.addView(chip)
        }

        binding.chipGroupCategorias.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            val categoriaId = checkedId?.let { group.findViewById<Chip>(it)?.tag as? Long }
            viewModel.onCategoriaSelected(categoriaId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

