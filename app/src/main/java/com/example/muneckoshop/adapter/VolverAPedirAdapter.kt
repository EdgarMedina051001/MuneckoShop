package com.example.muneckoshop.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.databinding.ComprarNuevamenteItemBinding

class VolverAPedirAdapter(private val volverPedirProductoNombre:MutableList<String>,
                          private val volverPedirProductoPrecio:MutableList<String>,
                          private val volverPedirProductoImagen:MutableList<String>,
                            private var requireContext: Context) :
    RecyclerView.Adapter<VolverAPedirAdapter.VolverAPedirViewHolder>() {

    override fun onBindViewHolder(holder: VolverAPedirViewHolder, position: Int) {
        holder.bind(volverPedirProductoNombre[position], volverPedirProductoPrecio[position], volverPedirProductoImagen[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolverAPedirViewHolder {
        val binding = ComprarNuevamenteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolverAPedirViewHolder(binding)
    }

    override fun getItemCount(): Int = volverPedirProductoNombre.size

    inner class VolverAPedirViewHolder(private val binding: ComprarNuevamenteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(ProductoNombre: String, ProductoPrecio: String, ProductoImagen: String) {
            binding.tvNombreProductoCN.text = ProductoNombre
            binding.tvPrecioProductoCN.text = ProductoPrecio
            val uriString = ProductoImagen
            val uri = Uri.parse(uriString)
            Glide.with(requireContext).load(uri).into(binding.ivImagenProductoCN)

        }

    }

}