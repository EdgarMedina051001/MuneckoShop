package com.example.muneckoshop.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.muneckoshop.Model.MenuItem
import com.example.muneckoshop.R
import com.example.muneckoshop.SplashScreenActivity
import com.example.muneckoshop.databinding.FragmentInicioBinding
import com.example.muneckoshop.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilFragment : Fragment() {
    private lateinit var binding: FragmentPerfilBinding

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPerfilBinding.inflate(inflater, container, false)

        //Traer datos del usuario
        traerDatosUsuario()

        binding.btnActualizarDatosPerfil.setOnClickListener {
            val nombre = binding.tietNombresPerfil.text.toString().trim()
            val correo = binding.tietCorreoPerfil.text.toString().trim()
            val direccion = binding.tietDireccionPerfil.text.toString().trim()
            val celular = binding.tietCelularPerfil.text.toString().trim()
            val contrasena = binding.tietContraseAPerfil.text.toString().trim()

            subirDatosUsuarioFirebase(nombre, correo, direccion, celular, contrasena)
        }

        binding.btnCerrarSesionPerfil.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent5 = Intent(
                requireContext(),
                SplashScreenActivity::class.java
            )
            startActivity(intent5)
        }

        return binding.root
    }

    private fun subirDatosUsuarioFirebase(
        nombre: String,
        correo: String,
        direccion: String,
        celular: String,
        contrasena: String
    ) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val usuarioRef = database.getReference("Usuarios").child(userId)
            val usuarioDatos = hashMapOf(
                "nombre" to nombre,
                "email" to correo,
                "celular" to celular,
                "password" to contrasena,
                "direccion" to direccion
            )
            usuarioRef.setValue(usuarioDatos).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Se actualizaron los datos correctamente.",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error. No se actualizaron los datos.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    private fun traerDatosUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            val usuarioId = usuario.uid
            val usuarioRef = database.getReference().child("Usuarios").child(usuarioId)

            usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nombres = snapshot.child("nombre").getValue(String::class.java) ?: ""
                        val direcciones =
                            snapshot.child("direccion").getValue(String::class.java) ?: ""
                        val celulares = snapshot.child("celular").getValue(String::class.java) ?: ""
                        val correos = snapshot.child("email").getValue(String::class.java) ?: ""
                        val contrasenas =
                            snapshot.child("password").getValue(String::class.java) ?: ""

                        binding.apply {
                            tietNombresPerfil.setText(nombres)
                            tietDireccionPerfil.setText(direcciones)
                            tietCelularPerfil.setText(celulares)
                            tietCorreoPerfil.setText(correos)
                            tietContraseAPerfil.setText(contrasenas)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}