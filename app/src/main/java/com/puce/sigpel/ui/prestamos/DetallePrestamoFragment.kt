package com.puce.sigpel.ui.prestamos

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.puce.sigpel.R

class DetallePrestamoFragment : Fragment(R.layout.fragment_detalle_prestamo) {

    private val viewModel: MisPrestamosViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarPrestamo)

        // Simulación o recepción de los datos del préstamo actual
        val estadoPrestamo = "pendiente" // Cambiará según el objeto recibido
        val prestamoId = "1"             // ID del préstamo actual

        // Validación según la HU-30: solo se muestra si está pendiente
        if (estadoPrestamo == "pendiente") {
            btnCancelar.visibility = View.VISIBLE
            btnCancelar.setOnClickListener {
                viewModel.cancelarPrestamo(prestamoId)
            }
        } else {
            btnCancelar.visibility = View.GONE
        }
    }
}