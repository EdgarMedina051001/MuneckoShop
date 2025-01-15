package com.example.muneckoshop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.Model.CategoriaModel
import com.example.muneckoshop.R

class CategoriaAdapter(private val categories: List<CategoriaModel>, private val onCategoryClick: (CategoriaModel) -> Unit) :
    RecyclerView.Adapter<CategoriaAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageCategory: ImageView = itemView.findViewById(R.id.ivCategoryItem)
        val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryItem)

        fun bind(category: CategoriaModel) {
            Glide.with(itemView.context)
                .load(category.imageUrl)
                .into(imageCategory)

            tvCategoryName.text = category.name

            itemView.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}