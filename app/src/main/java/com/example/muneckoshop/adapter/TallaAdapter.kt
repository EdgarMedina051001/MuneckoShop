package com.example.muneckoshop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.muneckoshop.R

class TallaAdapter(private val context: Context, private val tallas: List<String>, private val onClick: (String) -> Unit) :
    RecyclerView.Adapter<TallaAdapter.TallaViewHolder>() {

    private var selectedPosition = -1

    inner class TallaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTalla: TextView = itemView.findViewById(R.id.tvTalla)

        fun bind(talla: String, isSelected: Boolean) {
            tvTalla.text = talla
            itemView.isSelected = isSelected
            if (isSelected) {
                tvTalla.setBackgroundResource(R.drawable.talla_background_selected)
            } else {
                tvTalla.setBackgroundResource(R.drawable.background_talla)
            }
            itemView.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition = adapterPosition
                notifyItemChanged(selectedPosition)
                onClick(talla)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TallaViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.talla_item, parent, false)
        return TallaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TallaViewHolder, position: Int) {
        holder.bind(tallas[position], position == selectedPosition)
    }

    override fun getItemCount(): Int {
        return tallas.size
    }
}