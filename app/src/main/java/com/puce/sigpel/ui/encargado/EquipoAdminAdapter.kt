package com.puce.sigpel.ui.encargado

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.databinding.ItemEquipoAdminBinding
import com.puce.sigpel.ui.common.bindEstadoEquipo

class EquipoAdminAdapter(
    private val onCambiarEstado: (EquipoResponse) -> Unit,
    private val onEliminar: (EquipoResponse) -> Unit
) : ListAdapter<EquipoResponse, EquipoAdminAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEquipoAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEquipoAdminBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(equipo: EquipoResponse) {
            binding.textNombre.text = equipo.nombre
            binding.textCategoria.text = binding.root.context.getString(
                com.puce.sigpel.R.string.gestion_equipos_categoria_serial, equipo.categoriaNombre, equipo.numeroSerie ?: "-"
            )
            binding.badgeEstado.bindEstadoEquipo(equipo.estado)
            binding.buttonEditar.setOnClickListener { onCambiarEstado(equipo) }
            binding.buttonEliminar.setOnClickListener { onEliminar(equipo) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<EquipoResponse>() {
            override fun areItemsTheSame(oldItem: EquipoResponse, newItem: EquipoResponse) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: EquipoResponse, newItem: EquipoResponse) = oldItem == newItem
        }
    }
}
