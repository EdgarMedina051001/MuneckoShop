package com.example.muneckoshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.muneckoshop.Model.MenuItem
import com.example.muneckoshop.R
import com.example.muneckoshop.adapter.MenuAdapter
import com.example.muneckoshop.databinding.FragmentBuscarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BuscarFragment : Fragment() {

    private lateinit var binding: FragmentBuscarBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuscarBinding.inflate(inflater, container, false)
        //Configuración para SearchView
        confSearchView()
        //Mostrar todos los items(postres) del menú de firebase
        extraerMenuItem()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun extraerMenuItem() {
        //
        database = FirebaseDatabase.getInstance()
        //
        val postreRef: DatabaseReference = database.reference.child("Menú")
        postreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postreSnapshot in snapshot.children) {
                    val menuItem = postreSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        originalMenuItems.add(it)
                    }
                }
                mostrarTodoMenu()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun mostrarTodoMenu() {
        val filtradoMenuItem = ArrayList(originalMenuItems)
        asignarAdapter(filtradoMenuItem)
    }

    private fun asignarAdapter(filtradoMenuItem: List<MenuItem>) {
        adapter = MenuAdapter(filtradoMenuItem, requireContext())
        binding.menuRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.menuRecyclerView.adapter = adapter
    }

    private fun confSearchView() {
        binding.svBuscar.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filtroMenuItems(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filtroMenuItems(newText)
                return true
            }
        })
    }

    private fun filtroMenuItems(query: String) {
        val filtradosMenuItems = originalMenuItems.filter {
            it.productoNombre?.contains(query, ignoreCase = true) == true
        }
        asignarAdapter(filtradosMenuItems)
    }
}