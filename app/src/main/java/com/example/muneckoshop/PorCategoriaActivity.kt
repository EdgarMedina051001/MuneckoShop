package com.example.muneckoshop

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muneckoshop.adapter.ProductAdapter
import com.example.muneckoshop.databinding.ActivityPorCategoriaBinding
import com.example.muneckoshop.Model.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PorCategoriaActivity : AppCompatActivity() {
    private val binding: ActivityPorCategoriaBinding by lazy {
        ActivityPorCategoriaBinding.inflate(layoutInflater)
    }

    private var categoryId: String? = null
    private var categoryName: String? = null

    private lateinit var productAdapter: ProductAdapter
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val productsRef: DatabaseReference = database.getReference("Menú")
    private val productsList = mutableListOf<MenuItem>()

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

        binding.rvCategoryProduct.layoutManager = LinearLayoutManager(this)

        categoryId = intent.getStringExtra("categoryId")
        categoryName = intent.getStringExtra("categoryName")

        binding.tvCategoryName.text = "Categoria: " + categoryName

        productAdapter = ProductAdapter(productsList)
        binding.rvCategoryProduct.adapter = productAdapter

        fetchProducts()
    }

    private fun fetchProducts() {
        if (categoryId != null) {
            productsRef.orderByChild("productoCategoria").equalTo(categoryName)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        productsList.clear()
                        for (productSnapshot in snapshot.children) {
                            val product = productSnapshot.getValue(MenuItem::class.java)
                            product?.let { productsList.add(it) }
                        }
                        productAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Manejar error
                    }
                })
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}