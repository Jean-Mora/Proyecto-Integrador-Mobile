package com.puce.sigpel.ui.prestamos

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.puce.sigpel.R
import com.puce.sigpel.databinding.FragmentMisPrestamosBinding // Si usas ViewBinding, o usa findViewById si prefieres

class MisPrestamosFragment : Fragment(R.layout.fragment_mis_prestamos) {

    private val viewModel: MisPrestamosViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nota: Si usas RecyclerView directamente con findViewById:
        // val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerMisPrestamos)
        // recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.prestamos.observe(viewLifecycleOwner) { listaPrestamos ->
            // Aquí actualizarás el adaptador cuando lleguen los datos del GET /prestamos/me
            // recyclerView.adapter = PrestamoAdapter(listaPrestamos)
        }

        viewModel.cargarMisPrestamos()
    }
}