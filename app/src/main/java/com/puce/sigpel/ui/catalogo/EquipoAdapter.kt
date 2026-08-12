package com.puce.sigpel.ui.catalogo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.data.remote.dto.EquipoResponse
import com.puce.sigpel.databinding.ItemEquipoBinding
import com.puce.sigpel.ui.common.bindEstadoEquipo

class EquipoAdapter(private val onClick: (EquipoResponse) -> Unit) :
    ListAdapter<EquipoResponse, EquipoAdapter.EquipoViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipoViewHolder {
        val binding = ItemEquipoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EquipoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EquipoViewHolder(private val binding: ItemEquipoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(equipo: EquipoResponse) {
            binding.textNombre.text = equipo.nombre
            binding.textCategoria.text = equipo.categoriaNombre
            binding.badgeEstado.bindEstadoEquipo(equipo.estado)
            binding.root.setOnClickListener { onClick(equipo) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<EquipoResponse>() {
            override fun areItemsTheSame(oldItem: EquipoResponse, newItem: EquipoResponse) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: EquipoResponse, newItem: EquipoResponse) = oldItem == newItem
        }
    }
}

