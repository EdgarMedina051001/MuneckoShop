package com.example.muneckoshop.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.databinding.CarritoItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarritoAdapter(
    private val context: Context,
    private val carritoItems: MutableList<String>,
    private val carritoItemPrecio: MutableList<String>,
    private val carritoItemDescripcion: MutableList<String>,
    private val carritoItemImagen: MutableList<String>,
    private val carritoItemCantidad: MutableList<Int>,
    private val carritoItemTalla: MutableList<String>
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: ""
        val carritoItemNumero = carritoItems.size

        if (carritoItemNumero > 0) {
            itemCantidades = IntArray(carritoItemNumero) { 1 }
            carritoItemsRef =
                database.reference.child("Usuarios").child(userId).child("CarritoItems")
        }
    }

    companion object {
        private var itemCantidades: IntArray = intArrayOf()
        private lateinit var carritoItemsRef: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val binding = CarritoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarritoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = carritoItems.size

    fun obtenerItemActualizado(): MutableList<Int> {
        val postreCantidad = mutableListOf<Int>()
        postreCantidad.addAll(carritoItemCantidad)
        return postreCantidad
    }

    inner class CarritoViewHolder(private val binding: CarritoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val cantidad = itemCantidades[position]
                tvNombreProductoCarrito.text = carritoItems[position]
                tvPrecioProductoCarrito.text = carritoItemPrecio[position]
                val uriString = carritoItemImagen[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(civImagenProductoCarrito)
                tvCantidadProductoCarrito.text = cantidad.toString()

                btnMenos.setOnClickListener { disminuirCantidad(position) }
                btnMas.setOnClickListener { incrementarCantidad(position) }
                btnEliminar.setOnClickListener {
                    val itemPosicion = adapterPosition
                    if (itemPosicion != RecyclerView.NO_POSITION) {
                        eliminarItem(itemPosicion)
                    }
                }

            }
        }

        private fun disminuirCantidad(position: Int) {
            if (itemCantidades[position] > 1) {
                itemCantidades[position]--
                carritoItemCantidad[position] = itemCantidades[position]
                binding.tvCantidadProductoCarrito.text = itemCantidades[position].toString()
            }
        }

        private fun incrementarCantidad(position: Int) {
            if (itemCantidades[position] < 10) {
                itemCantidades[position]++
                carritoItemCantidad[position] = itemCantidades[position]
                binding.tvCantidadProductoCarrito.text =
                    itemCantidades[position].toString()
            }
        }

        private fun eliminarItem(position: Int) {
            val recuperarPosicion = position
            obtenerLlaveUnicaEnPosicion(recuperarPosicion) { llaveUnica ->
                if (llaveUnica != null) {
                    removerItem(position, llaveUnica)
                }

            }

        }

        private fun removerItem(position: Int, llaveUnica: String) {
            if (llaveUnica != null) {
                carritoItemsRef.child(llaveUnica).removeValue().addOnSuccessListener {
                    carritoItems.removeAt(position)
                    carritoItemPrecio.removeAt(position)
                    carritoItemDescripcion.removeAt(position)
                    carritoItemImagen.removeAt(position)
                    carritoItemCantidad.removeAt(position)
                    carritoItemTalla.removeAt(position)
                    Toast.makeText(context, "Removido del Carrito.", Toast.LENGTH_SHORT).show()

                    //Actualizar Cantidades Item
                    itemCantidades =
                        itemCantidades.filterIndexed { index, i -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, carritoItems.size)
                }.addOnFailureListener {
                    Toast.makeText(context, "Fallo al Remover.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun obtenerLlaveUnicaEnPosicion(
            recuperarPosicion: Int,
            onComplete: (String?) -> Unit
        ) {
            carritoItemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var llaveUnica: String? = null
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if (index == recuperarPosicion) {
                            llaveUnica = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(llaveUnica)
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }


    }
}