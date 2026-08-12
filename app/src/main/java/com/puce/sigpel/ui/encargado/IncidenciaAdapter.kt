package com.puce.sigpel.ui.encargado

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.IncidenciaResponse
import com.puce.sigpel.databinding.ItemIncidenciaBinding
import com.puce.sigpel.ui.common.tipoIncidenciaLabel
import com.puce.sigpel.util.DateFormat

class IncidenciaAdapter(
    private val onEliminar: (IncidenciaResponse) -> Unit
) : ListAdapter<IncidenciaResponse, IncidenciaAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncidenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemIncidenciaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(incidencia: IncidenciaResponse) {
            val context = binding.root.context
            binding.textPrestamo.text = context.getString(R.string.gestion_incidencias_prestamo, incidencia.prestamoId)
            binding.badgeTipo.text = context.getString(tipoIncidenciaLabel(incidencia.tipo))
            binding.textDescripcion.text = incidencia.descripcion ?: context.getString(R.string.gestion_incidencias_sin_descripcion)
            binding.textFecha.text = context.getString(R.string.gestion_incidencias_fecha, DateFormat.formatDateTime(incidencia.fechaReporte))
            binding.buttonEliminar.setOnClickListener { onEliminar(incidencia) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<IncidenciaResponse>() {
            override fun areItemsTheSame(oldItem: IncidenciaResponse, newItem: IncidenciaResponse) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: IncidenciaResponse, newItem: IncidenciaResponse) = oldItem == newItem
        }
    }
}
