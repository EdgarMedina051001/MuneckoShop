package com.example.muneckoshop.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.muneckoshop.DetallesActivity
import com.example.muneckoshop.databinding.PlantillaItemBinding

class RecomendadoAdapter (private val items:List<String>, private val precio:List<String>, private val imagen:List<Int>, private val requireContext: Context): RecyclerView.Adapter<RecomendadoAdapter.RecomendadoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecomendadoViewHolder {
        return RecomendadoViewHolder(PlantillaItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: RecomendadoViewHolder, position: Int) {
        val item = items[position]
        val imagenes = imagen[position]
        val precio = precio[position]
        holder.bind(item,precio, imagenes)

        holder.itemView.setOnClickListener {
            //onClickListener para Activity Detalles
            val intent = Intent(requireContext, DetallesActivity::class.java)
            intent.putExtra("ProductoNombre", item)
            intent.putExtra("ProductoPrecio", precio)
            intent.putExtra("ProductoImagen", imagenes)
            requireContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class RecomendadoViewHolder(private val binding: PlantillaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imagenView = binding.civImagenProductoRecom
        fun bind(item: String, precio: String, imagenes: Int) {
            binding.tvNombreProductoRecom.text = item
            binding.tvPrecioProductoRecom.text = precio
            imagenView.setImageResource(imagenes)
        }

    }
}