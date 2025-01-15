package com.example.muneckoshop.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muneckoshop.Model.CarritoItems
import com.example.muneckoshop.PagoActivity
import com.example.muneckoshop.adapter.CarritoAdapter
import com.example.muneckoshop.databinding.FragmentCarritoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarritoFragment : Fragment() {
    private lateinit var binding: FragmentCarritoBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var productoNombres: MutableList<String>
    private lateinit var productoPrecios: MutableList<String>
    private lateinit var productoImagenesUri: MutableList<String>
    private lateinit var productoDescripciones: MutableList<String>
    private lateinit var productoTallas: MutableList<String>
    private lateinit var cantidad: MutableList<Int>
    private lateinit var carritoAdapter: CarritoAdapter
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCarritoBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        recuperarCarritoItems()

        binding.btnContinuar.setOnClickListener {
            //Obtener los detalles de la orden despues del carrito.
            ObtenerDetallesItemsOrden()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun ObtenerDetallesItemsOrden() {

        val ordenIdReference: DatabaseReference =
            database.reference.child("Usuarios").child(userId).child("CarritoItems")

        val productoNombre = mutableListOf<String>()
        val productoPrecio = mutableListOf<String>()
        val productoDescripcion = mutableListOf<String>()
        val productoImagen = mutableListOf<String>()
        val productoTalla = mutableListOf<String>()
        //Obtener cantidad item
        val productoCantidad = carritoAdapter.obtenerItemActualizado()

        ordenIdReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productoSnapshot in snapshot.children) {
                    val ordenItems = productoSnapshot.getValue(CarritoItems::class.java)

                    ordenItems?.productoNombre?.let { productoNombre.add(it) }
                    ordenItems?.productoPrecio?.let { productoPrecio.add(it) }
                    ordenItems?.productoDescripcion?.let { productoDescripcion.add(it) }
                    ordenItems?.productoImagen?.let { productoImagen.add(it) }
                    ordenItems?.productoTalla?.let { productoTalla.add(it) }
                }
                ordenarAhora(
                    productoNombre,
                    productoPrecio,
                    productoDescripcion,
                    productoImagen,
                    productoTalla,
                    productoCantidad
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Algo salio mal. Intenta otra vez.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

    }

    private fun ordenarAhora(
        productoNombre: MutableList<String>,
        productoPrecio: MutableList<String>,
        productoDescripcion: MutableList<String>,
        productoImagen: MutableList<String>,
        productoTalla: MutableList<String>,
        productoCantidad: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PagoActivity::class.java)
            intent.putExtra("ProductoItemNombre", productoNombre as ArrayList<String>)
            intent.putExtra("ProductoItemPrecio", productoPrecio as ArrayList<String>)
            intent.putExtra("ProductoItemImagen", productoImagen as ArrayList<String>)
            intent.putExtra("ProductoItemDescripcion", productoDescripcion as ArrayList<String>)
            intent.putExtra("ProductoItemTalla", productoTalla as ArrayList<String>)
            intent.putExtra("ProductoItemCantidad", productoCantidad as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun recuperarCarritoItems() {
        //
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val productoRef: DatabaseReference =
            database.reference.child("Usuarios").child(userId).child("CarritoItems")

        productoNombres = mutableListOf()
        productoPrecios = mutableListOf()
        productoDescripciones = mutableListOf()
        productoImagenesUri = mutableListOf()
        productoTallas = mutableListOf()
        cantidad = mutableListOf()

        productoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (productoSnapshot in snapshot.children) {
                    val carritoItems = productoSnapshot.getValue(CarritoItems::class.java)

                    carritoItems?.productoNombre?.let { productoNombres.add(it) }
                    carritoItems?.productoPrecio?.let { productoPrecios.add(it) }
                    carritoItems?.productoDescripcion?.let { productoDescripciones.add(it) }
                    carritoItems?.productoImagen?.let { productoImagenesUri.add(it) }
                    carritoItems?.productoCantidad?.let { cantidad.add(it) }
                    carritoItems?.productoTalla?.let { productoTallas.add(it) }
                }

                setAdapter()
            }

            private fun setAdapter() {
                carritoAdapter = CarritoAdapter(
                    requireContext(),
                    productoNombres,
                    productoPrecios,
                    productoDescripciones,
                    productoImagenesUri,
                    cantidad,
                    productoTallas
                )
                binding.carritorecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.carritorecyclerView.adapter = carritoAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Dato no traido.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}