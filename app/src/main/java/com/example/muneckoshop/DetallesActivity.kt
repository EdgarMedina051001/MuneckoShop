package com.example.muneckoshop

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muneckoshop.Model.CarritoItems
import com.example.muneckoshop.adapter.TallaAdapter
import com.example.muneckoshop.databinding.ActivityDetallesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetallesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetallesBinding

    private var productoDescripcion : String? = null
    private var productoImagen : String? = null
    private var productoNombre : String? = null
    private var productoPrecio : String? = null
    private var productoTalla : String? = null
    private lateinit var auth : FirebaseAuth

    private lateinit var tallaAdapter: TallaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetallesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Lista de tallas
        val tallas = listOf("Unitalla")

        tallaAdapter = TallaAdapter(this, tallas) { selectedTalla ->
            // Acción al seleccionar una talla
            Toast.makeText(this, "Talla seleccionada: $selectedTalla", Toast.LENGTH_SHORT).show()
            productoTalla = selectedTalla
        }

        binding.rvTallas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTallas.adapter = tallaAdapter

        binding.btnAgregarCarritoDetalles.setOnClickListener {
            agregarItemAlCarrito()
        }

        productoNombre = intent.getStringExtra("MenuItemNombre")
        productoPrecio = intent.getStringExtra("MenuItemPrecio")
        productoDescripcion = intent.getStringExtra("MenuItemDescripcion")
        productoImagen = intent.getStringExtra("MenuItemImagen")
        productoTalla = intent.getStringExtra("MenuItemTalla")

        with(binding){
            tvNombrePostreDetalles.text=productoNombre
            tvPrecioPostreDetalles.text=productoPrecio
            tvDescripcionPostreDetalles.text=productoDescripcion
            Glide.with(this@DetallesActivity).load(Uri.parse(productoImagen)).into(ivImagenPostreDetalles)
        }
    }

    private fun agregarItemAlCarrito() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid?:""

        if (productoTalla.isNullOrEmpty()) {
            Toast.makeText(this, "Por favor, selecciona una talla.", Toast.LENGTH_SHORT).show()
            return
        }

        //Crear un objeto Carrito
        val carritoItem = CarritoItems(productoNombre.toString(), productoPrecio.toString(), productoDescripcion.toString(),productoImagen.toString(), 1, productoTalla.toString())
        //Guardar datos para el carrito de Firebase database
        database.child("Usuarios").child(userId).child("CarritoItems").push().setValue(carritoItem).addOnSuccessListener {
            Toast.makeText(this, "Se ha agregado al carrito correctamente.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "No se agrego al carrito.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}