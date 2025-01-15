package com.example.muneckoshop.fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.muneckoshop.Model.DetallesOrden
import com.example.muneckoshop.adapter.VolverAPedirAdapter
import com.example.muneckoshop.databinding.FragmentHistorialBinding
import com.example.muneckoshop.ordenRecienteItemsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistorialFragment : Fragment() {
    private lateinit var binding: FragmentHistorialBinding
    private lateinit var volverAPedirAdapter: VolverAPedirAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private var listaDeOrdenItem: ArrayList<DetallesOrden> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistorialBinding.inflate(layoutInflater, container, false)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //Recuperar y mostrar Historial Usuario
        recuperarHistorialCompra()

        binding.clCompraRecienteHistorial.setOnClickListener {
            verItemsCompraReciente()
        }

        binding.btnAceptarOrdenHistorial.setOnClickListener {
            actualizarStatusOrden()
        }

        return binding.root
    }

    private fun actualizarStatusOrden() {
        val itemPushKey = listaDeOrdenItem[0].itemPushLlave
        val ordenCompletadaReferencia = database.reference.child("OrdenCompletada").child(itemPushKey!!)
        ordenCompletadaReferencia.child("pagoRecibido").setValue(true)
        Toast.makeText(requireContext(), "Orden Completada", Toast.LENGTH_SHORT).show()
    }

    private fun verItemsCompraReciente() {
        listaDeOrdenItem.reverse()?.let {compraReciente ->
            val intent = Intent(requireContext(), ordenRecienteItemsActivity::class.java)
            intent.putExtra("CompraRecienteOrdenItem", listaDeOrdenItem)
            startActivity(intent)
        }
    }

    private fun recuperarHistorialCompra() {
        binding.clCompraRecienteHistorial.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""

        val compraItemReferencia: DatabaseReference =
            database.reference.child("Usuarios").child(userId).child("HistorialCompra")
        val shortingQuery = compraItemReferencia.orderByChild("tiempoActual")

        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (compraSnapshot in snapshot.children) {
                    val compraHistorialItem = compraSnapshot.getValue(DetallesOrden::class.java)
                    compraHistorialItem?.let {
                        listaDeOrdenItem.add(it)
                    }
                }
                listaDeOrdenItem.reverse()
                if (listaDeOrdenItem.isNotEmpty()) {
                    //
                    asignarValoresCompraRecienteItem()
                    //
                    asignarCompraAnteriorItemsRecyclerView()

                    //Log para revisar si los items estan correctos
                    Log.d("ComprasRecientesOrdenItems", "Items Recibidos: ")
                    for ((i , item) in listaDeOrdenItem.withIndex()){
                        Log.d("ComprasRecientesOrdenItems", "Item $i")
                        Log.d("ComprasRecientesOrdenItemsNombre", "Item ${item.productoNombres?.joinToString ( ", " )}")
                        Log.d("ComprasRecientesOrdenItemsImagen", "Item ${item.productoImagenes?.joinToString ( ", " )}")
                        Log.d("ComprasRecientesOrdenItemsPrecio", "Item ${item.productoPrecios?.joinToString ( ", " )}")
                        Log.d("ComprasRecientesOrdenItemsTalla", "Item ${item.productoTallas?.joinToString ( ", " )}")
                        Log.d("ComprasRecientesOrdenItemsCantidad", "Item ${item.productoCantidades?.joinToString ( ", " )}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun asignarValoresCompraRecienteItem() {
        binding.clCompraRecienteHistorial.visibility = View.VISIBLE
        val ordenRecienteItem = listaDeOrdenItem.firstOrNull()
        ordenRecienteItem?.let {
            with(binding) {
                tvNombrePostreHistorial.text = it.productoNombres?.firstOrNull() ?: ""
                tvPrecioPostreHistorial.text = it.productoPrecios?.firstOrNull() ?: ""
                val imagen = it.productoImagenes?.firstOrNull() ?: ""
                val uri = Uri.parse(imagen)
                Glide.with(requireContext()).load(uri).into(ivImagenPostreHistorial)

                listaDeOrdenItem.firstOrNull()
                /*if (listaDeOrdenItem.isNotEmpty()) { }*/

                val esOrdenEsAceptada = listaDeOrdenItem[0].ordenAceptada
                if (esOrdenEsAceptada){
                    cvOrdenStatusHistorial.background.setTint(Color.GREEN)
                    btnAceptarOrdenHistorial.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun asignarCompraAnteriorItemsRecyclerView() {
        val volverAPedirPostreNombre = mutableListOf<String>()
        val volverAPedirPostrePrecio = mutableListOf<String>()
        val volverAPedirPostreImagen = mutableListOf<String>()
        for (i in 1 until listaDeOrdenItem.size) {
            listaDeOrdenItem[i].productoNombres?.firstOrNull()?.let {
                volverAPedirPostreNombre.add(it)
                listaDeOrdenItem[i].productoPrecios?.firstOrNull()?.let {
                    volverAPedirPostrePrecio.add(it)
                    listaDeOrdenItem[i].productoImagenes?.firstOrNull()?.let {
                        volverAPedirPostreImagen.add(it)
                    }
                }

                val recyclerView = binding.historialRecyclerView
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                volverAPedirAdapter = VolverAPedirAdapter(
                    volverAPedirPostreNombre,
                    volverAPedirPostrePrecio,
                    volverAPedirPostreImagen,
                    requireContext()
                )
                recyclerView.adapter = volverAPedirAdapter

            }
        }
    }
    
}