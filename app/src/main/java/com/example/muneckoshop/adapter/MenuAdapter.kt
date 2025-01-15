package com.example.muneckoshop.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.DetallesActivity
import com.example.muneckoshop.databinding.MenuItemBinding
import com.example.muneckoshop.Model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    abrirActivityDetalles(position)
                }
            }
        }

        private fun abrirActivityDetalles(position: Int) {
            val menuItem = menuItems[position]
            //
            val intent = Intent(requireContext, DetallesActivity::class.java).apply {
                putExtra("MenuItemNombre", menuItem.productoNombre)
                putExtra("MenuItemImagen", menuItem.productoImagen)
                putExtra("MenuItemDescripcion", menuItem.productoDescripcion)
                putExtra("MenuItemIngredientes", menuItem.productoTalla)
                putExtra("MenuItemPrecio", menuItem.productoPrecio)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                tvNombreProductoMenu.text = menuItem.productoNombre
                tvPrecioProductoMenu.text = menuItem.productoPrecio
                val uri = Uri.parse(menuItem.productoImagen)
                Glide.with(requireContext).load(uri).into(civImagenProductoMenu)
            }
        }

    }
}
