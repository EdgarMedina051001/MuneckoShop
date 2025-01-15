package com.example.muneckoshop.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.DetallesActivity
import com.example.muneckoshop.R
import com.example.muneckoshop.Model.MenuItem

class ProductAdapter(private val products: List<MenuItem>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.ivItemCategoryProduct)
        val tvProductName: TextView = itemView.findViewById(R.id.tvNameCategoryProduct)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvPrecioCategoryProduct)

        fun bind(product: MenuItem) {
            Glide.with(itemView.context)
                .load(product.productoImagen)
                .into(imageProduct)

            tvProductName.text = product.productoNombre
            tvProductPrice.text = product.productoPrecio

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetallesActivity::class.java).apply {
                    putExtra("MenuItemNombre", product.productoNombre)
                    putExtra("MenuItemPrecio", product.productoPrecio)
                    putExtra("MenuItemDescripcion", product.productoDescripcion)
                    putExtra("MenuItemTalla", product.productoTalla)
                    putExtra("MenuItemImagen", product.productoImagen)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_product_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }
}