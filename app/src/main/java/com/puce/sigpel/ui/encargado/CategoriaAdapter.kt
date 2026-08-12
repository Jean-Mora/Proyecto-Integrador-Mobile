package com.puce.sigpel.ui.encargado

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puce.sigpel.data.remote.dto.CategoriaEquipoResponse
import com.puce.sigpel.databinding.ItemCategoriaBinding

class CategoriaAdapter(
    private val onEditar: (CategoriaEquipoResponse) -> Unit,
    private val onEliminar: (CategoriaEquipoResponse) -> Unit
) : ListAdapter<CategoriaEquipoResponse, CategoriaAdapter.ViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCategoriaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoria: CategoriaEquipoResponse) {
            binding.textNombre.text = categoria.nombre
            binding.buttonEditar.setOnClickListener { onEditar(categoria) }
            binding.buttonEliminar.setOnClickListener { onEliminar(categoria) }
        }
    }

    private companion object {
        val DIFF = object : DiffUtil.ItemCallback<CategoriaEquipoResponse>() {
            override fun areItemsTheSame(oldItem: CategoriaEquipoResponse, newItem: CategoriaEquipoResponse) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: CategoriaEquipoResponse, newItem: CategoriaEquipoResponse) = oldItem == newItem
        }
    }
}
