package com.puce.sigpel.ui.prestamos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.R

// Reemplaza 'Any' por tu DTO o modelo real de Préstamo cuando lo tengas
class PrestamoAdapter(private val prestamos: List<Any>) :
    RecyclerView.Adapter<PrestamoAdapter.PrestamoViewHolder>() {

    class PrestamoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEquipoNombre: TextView = view.findViewById(R.id.tvEquipoNombre)
        val tvPrestamoEstado: TextView = view.findViewById(R.id.tvPrestamoEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prestamo, parent, false)
        return PrestamoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        val prestamo = prestamos[position]
        // Aquí asignarás los datos reales del préstamo a las vistas, por ejemplo:
        // holder.tvEquipoNombre.text = prestamo.equipoNombre
        // holder.tvPrestamoEstado.text = prestamo.estado
    }

    override fun getItemCount(): Int = prestamos.size
}