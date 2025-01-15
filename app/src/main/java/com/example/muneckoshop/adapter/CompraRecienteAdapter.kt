package com.example.muneckoshop.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.databinding.CompraRecienteItemBinding

class CompraRecienteAdapter(
    private var context: Context,
    private var postreNombreLista: ArrayList<String>,
    private var postreImagenLista: ArrayList<String>,
    private var postrePrecioLista: ArrayList<String>,
    private var postreTallaLista: ArrayList<String>,
    private var postreCantidadLista: ArrayList<Int>
) : RecyclerView.Adapter<CompraRecienteAdapter.CompraRecienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompraRecienteViewHolder {
        val binding = CompraRecienteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CompraRecienteViewHolder(binding)
    }

    override fun getItemCount(): Int = postreNombreLista.size

    override fun onBindViewHolder(holder: CompraRecienteViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CompraRecienteViewHolder(private val binding: CompraRecienteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNombreProductoCR.text = postreNombreLista[position]
                tvPrecioProductoCR.text = postrePrecioLista[position]
                tvCantidadNumProductoCR.text = postreCantidadLista[position].toString()
                val uriString = postreImagenLista[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(civImagenProductoCR)
            }

        }

    }

}