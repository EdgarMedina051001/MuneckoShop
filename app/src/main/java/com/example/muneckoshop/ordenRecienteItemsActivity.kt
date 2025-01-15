package com.example.muneckoshop

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muneckoshop.Model.DetallesOrden
import com.example.muneckoshop.adapter.CompraRecienteAdapter
import com.example.muneckoshop.databinding.ActivityOrdenRecienteItemsBinding

class ordenRecienteItemsActivity : AppCompatActivity() {

    private val binding: ActivityOrdenRecienteItemsBinding by lazy {
        ActivityOrdenRecienteItemsBinding.inflate(layoutInflater)
    }

    private lateinit var todosProductoNombres: ArrayList<String>
    private lateinit var todosProductoPrecios: ArrayList<String>
    private lateinit var todosProductoImagenes: ArrayList<String>
    private lateinit var todosProductoTallas: ArrayList<String>
    private lateinit var todosProductoCantidades: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Recuperar datos de la orden
        val ordenRecienteItems =
            intent.getSerializableExtra("CompraRecienteOrdenItem") as ArrayList<DetallesOrden>
        ordenRecienteItems?.let { detallesOrden ->
            if (detallesOrden.isNotEmpty()) {
                val ordenRecienteItem = detallesOrden[0]
                todosProductoNombres = ordenRecienteItem.productoNombres as ArrayList<String>
                todosProductoImagenes = ordenRecienteItem.productoImagenes as ArrayList<String>
                todosProductoPrecios = ordenRecienteItem.productoPrecios as ArrayList<String>
                todosProductoTallas = ordenRecienteItem.productoTallas as ArrayList<String>
                todosProductoCantidades = ordenRecienteItem.productoCantidades as ArrayList<Int>

            }
        }
        asignarAdapter()
    }

    private fun asignarAdapter() {
        val recyclerView = binding.ordenRecienteRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CompraRecienteAdapter(this, todosProductoNombres, todosProductoImagenes,todosProductoPrecios, todosProductoTallas, todosProductoCantidades)
        recyclerView.adapter = adapter
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