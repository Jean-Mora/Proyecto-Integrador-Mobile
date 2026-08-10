package com.puce.sigpel.ui.prestamos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.databinding.ItemPrestamoBinding
import com.puce.sigpel.ui.common.bindEstadoPrestamo
import com.puce.sigpel.util.DateFormat

class PrestamoAdapter(private val onClick: (PrestamoResponse) -> Unit) :
    ListAdapter<PrestamoResponse, PrestamoAdapter.PrestamoViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrestamoViewHolder {
        val binding = ItemPrestamoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PrestamoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrestamoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PrestamoViewHolder(private val binding: ItemPrestamoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prestamo: PrestamoResponse) {
            binding.textEquipo.text = prestamo.equipoNombre
            binding.badgeEstado.bindEstadoPrestamo(prestamo.estado)
            binding.textFechaSolicitud.text = binding.root.context.getString(
                R.string.detalle_prestamo_fecha_solicitud,
                DateFormat.formatDateTime(prestamo.fechaSolicitud)
            )
            binding.root.setOnClickListener { onClick(prestamo) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<PrestamoResponse>() {
            override fun areItemsTheSame(oldItem: PrestamoResponse, newItem: PrestamoResponse) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: PrestamoResponse, newItem: PrestamoResponse) = oldItem == newItem
        }
    }
}
