package com.puce.sigpel.ui.encargado

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puce.sigpel.R

class RegistrarEquipoFragment : Fragment(R.layout.fragment_registrar_equipo) {

    private val viewModel: PrestamoAdminViewModel by viewModels() // O un ViewModel dedicado si prefieres

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNombreEquipo = view.findViewById<EditText>(R.id.etNombreEquipo)
        val etSerialEquipo = view.findViewById<EditText>(R.id.etSerialEquipo)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarEquipo)

        // Dentro del setOnClickListener en RegistrarEquipoFragment.kt
        btnGuardar.setOnClickListener {
            val nombre = etNombreEquipo.text.toString()
            val serial = etSerialEquipo.text.toString()

            if (nombre.isNotEmpty() && serial.isNotEmpty()) {
                viewModel.registrarEquipo(nombre, serial)
            } else {
                // Opcional: Mostrar error si los campos están vacíos
            }
        }
    }
}