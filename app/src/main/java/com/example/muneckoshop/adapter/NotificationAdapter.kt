package com.example.muneckoshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.muneckoshop.databinding.NotificacionItemBinding

class NotificationAdapter (private var notificacion: ArrayList<String>, private var notificacionImagen : ArrayList<Int>) : RecyclerView.Adapter<NotificationAdapter.NotificacionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val binding = NotificacionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificacionViewHolder(binding)
    }

    override fun getItemCount(): Int = notificacion.size

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class NotificacionViewHolder(private val binding: NotificacionItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                tvNotificacion.text = notificacion[position]
                ivNotificacion.setImageResource(notificacionImagen[position])
            }

        }

    }
}