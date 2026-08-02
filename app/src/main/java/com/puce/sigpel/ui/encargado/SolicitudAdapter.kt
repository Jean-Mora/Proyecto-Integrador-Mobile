package com.puce.sigpel.ui.encargado

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.R
import com.puce.sigpel.data.remote.dto.EstadoPrestamo
import com.puce.sigpel.data.remote.dto.PrestamoResponse
import com.puce.sigpel.databinding.ItemSolicitudBinding
import com.puce.sigpel.ui.common.bindEstadoPrestamo
import com.puce.sigpel.ui.common.setVisible
import com.puce.sigpel.util.DateFormat

class SolicitudAdapter(
    private val onAprobar: (PrestamoResponse) -> Unit,
    private val onRechazar: (PrestamoResponse) -> Unit,
    private val onDevuelto: (PrestamoResponse) -> Unit
) : ListAdapter<PrestamoResponse, SolicitudAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSolicitudBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSolicitudBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(prestamo: PrestamoResponse) {
            binding.textEquipo.text = prestamo.equipoNombre
            binding.badgeEstado.bindEstadoPrestamo(prestamo.estado)
            binding.textSolicitante.text = binding.root.context.getString(R.string.solicitudes_solicitante, prestamo.estudianteUser)
            binding.textFechaSolicitud.text = binding.root.context.getString(
                R.string.detalle_prestamo_fecha_solicitud,
                DateFormat.formatDateTime(prestamo.fechaSolicitud)
            )

            binding.actionsPendiente.setVisible(prestamo.estado == EstadoPrestamo.PENDIENTE)
            binding.buttonDevuelto.setVisible(prestamo.estado == EstadoPrestamo.APROBADO)

            binding.buttonAprobar.setOnClickListener { onAprobar(prestamo) }
            binding.buttonRechazar.setOnClickListener { onRechazar(prestamo) }
            binding.buttonDevuelto.setOnClickListener { onDevuelto(prestamo) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<PrestamoResponse>() {
            override fun areItemsTheSame(oldItem: PrestamoResponse, newItem: PrestamoResponse) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: PrestamoResponse, newItem: PrestamoResponse) = oldItem == newItem
        }
    }
}
