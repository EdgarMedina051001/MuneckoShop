package com.example.muneckoshop.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.muneckoshop.DetallesActivity
import com.example.muneckoshop.MenuBottomSheetFragment
import com.example.muneckoshop.Model.CategoriaModel
import com.example.muneckoshop.PorCategoriaActivity
import com.example.muneckoshop.R
import com.example.muneckoshop.adapter.CategoriaAdapter
import com.example.muneckoshop.adapter.MenuAdapter
import com.example.muneckoshop.databinding.FragmentInicioBinding
import com.example.muneckoshop.Model.MenuItem
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.concurrent.thread

class InicioFragment : Fragment() {
    private lateinit var binding: FragmentInicioBinding
    private lateinit var menuItems: MutableList<MenuItem>

    private lateinit var categoryAdapter: CategoriaAdapter
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val categoriesRef: DatabaseReference = database.getReference("Categorias")
    private val categoriesList = mutableListOf<CategoriaModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInicioBinding.inflate(layoutInflater)

        fetchImagesFromFirestore()

        binding.tvVerMenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "TEST")
        }

        binding.categoryRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        categoryAdapter = CategoriaAdapter(categoriesList) { category ->
            openProductsActivity(category)
        }
        binding.categoryRecycler.adapter = categoryAdapter

        fetchCategories()


        //
        recuperarMostrarRecomendadoItems()
        return binding.root
    }

    private fun ejecutarFuncionConstantemente() {
        thread {
            while (true) {
                fetchImagesFromFirestore()
                fetchCategories()
                Thread.sleep(3000) // Esperar 1 segundo antes de volver a ejecutar
            }
        }
    }

    private fun fetchCategories() {
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriesList.clear()
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(CategoriaModel::class.java)
                    category?.let { categoriesList.add(it) }
                }
                categoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar error
            }
        })
    }

    private fun openProductsActivity(category: CategoriaModel) {
        val intent = Intent(requireContext(), PorCategoriaActivity::class.java).apply {
            putExtra("categoryId", category.id)
            putExtra("categoryName", category.name)
        }
        startActivity(intent)
    }


    private fun fetchImagesFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Slider")
            .get()
            .addOnSuccessListener { result ->
                val imageList = mutableListOf<SlideModel>()
                for (document in result) {
                    val imageUrl = document.getString("imageUrl")
                    if (imageUrl != null) {
                        // Agregar la imagen a la lista
                        imageList.add(SlideModel(imageUrl, ScaleTypes.CENTER_CROP))
                    }
                }
                binding.ivSlider.setImageList(imageList)
            }
            .addOnFailureListener { exception ->
                // Manejar errores
            }
    }

    private fun recuperarMostrarRecomendadoItems() {
        //Obtener referencia de la base de datos
        val postreRef = database.reference.child("Menú")
        menuItems = mutableListOf()

        //Obtener items del menu de la base de datos
        postreRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postreSnapshot in snapshot.children){
                    val menuItem = postreSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                //Mostrar un postre random
                randomPostreItem()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun randomPostreItem() {
        //Crear como shuffled list del menu items
        val index = menuItems.indices.toList().shuffled()
        val numItemParaMostrar = 6
        val subsetMenuItems = index.take(numItemParaMostrar).map { menuItems[it] }

        setRecomendadoItemsAdapter(subsetMenuItems)
    }

    private fun setRecomendadoItemsAdapter(subsetMenuItems: List<MenuItem>) {
        val adapter = MenuAdapter(subsetMenuItems, requireContext())
        binding.recomendadoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recomendadoRecyclerView.adapter = adapter
    }
}