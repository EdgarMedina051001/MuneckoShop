package com.example.muneckoshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muneckoshop.Model.UsuarioModel
import com.example.muneckoshop.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var nombre: String
    private lateinit var celular: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

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

        //Inicializar FirebaseAuth
        auth = Firebase.auth
        //Inicializar Firebase Database
        database = Firebase.database.reference

        binding.btnRegistrarCuentaSign.setOnClickListener {
            nombre = binding.tietNombreSign.text.toString().trim()
            celular = binding.tietCelularSign.text.toString().trim()
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isEmpty() || celular.isEmpty() || celular.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                crearCuenta(email, password)
            }
        }

        binding.btnAccederCuenta.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun crearCuenta(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Se creo la cuenta correctamente.", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al crear la cuenta.", Toast.LENGTH_SHORT).show()
                Log.d("Cuenta", "crearCuenta: Fallo", task.exception)
            }
        }
    }

    private fun saveUserData() {
        //Obtener el texto de los EditText
        email = binding.email.text.toString().trim()
        celular = binding.tietCelularSign.text.toString().trim()
        nombre = binding.tietNombreSign.text.toString().trim()
        password = binding.password.text.toString().trim()

        val usuario = UsuarioModel(nombre, email, password, celular)
        val usuarioId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("Usuarios").child(usuarioId).setValue(usuario)
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