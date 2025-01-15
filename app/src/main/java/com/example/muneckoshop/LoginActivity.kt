package com.example.muneckoshop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muneckoshop.Model.UsuarioModel
import com.example.muneckoshop.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private var usuarioNombre: String ?= null
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Inicializar Firebase Auth
        auth = Firebase.auth
        //Inicializar la base de datos de Firebase
        database = Firebase.database.reference
        //Inicializar Google Sign-In
        binding.btnCrearCuenta.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnAccederCuentaLogin.setOnClickListener{
            //Obtener el texto de los EditText
            email = binding.tietCorreoLogin.text.toString().trim()
            password = binding.tietContrasenaLogin.text.toString().trim()

            if (email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Logeado Correctamente.", Toast.LENGTH_SHORT).show()
                crearUsuarioCuenta(email, password)
            }
        }

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun crearUsuarioCuenta(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                val usuario = auth.currentUser
                updateUi(usuario)
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val usuario = auth.currentUser
                        guardarUsuarioDatos()
                        updateUi(usuario)
                    }else{
                        Toast.makeText(this, "No se pudo Iniciar Sesión.", Toast.LENGTH_SHORT).show()
                        Log.d("Cuenta", "crearCuentaUsuario: Auntenticacion Fallida.", task.exception)
                    }
                }
            }
        }
    }

    private fun guardarUsuarioDatos() {
        //Obtener el texto de los EditText
        email = binding.tietCorreoLogin.text.toString().trim()
        password = binding.tietContrasenaLogin.text.toString().trim()

        val usuario = UsuarioModel(usuarioNombre, email, password)
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid
        usuarioId?.let {
            database.child("Usuarios").child(it).setValue(usuario)
        }
    }

    private fun updateUi(usuario: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}