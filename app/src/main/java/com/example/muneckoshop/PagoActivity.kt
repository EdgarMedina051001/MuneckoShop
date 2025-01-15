package com.example.muneckoshop

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.muneckoshop.Model.DetallesOrden
import com.example.muneckoshop.databinding.ActivityPagoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PagoActivity : AppCompatActivity() {
    lateinit var binding: ActivityPagoBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var nombre: String
    private lateinit var direccion: String
    private lateinit var celular: String
    private lateinit var montoTotal: String
    private lateinit var metodoPago: String
    private lateinit var productoItemNombre: ArrayList<String>
    private lateinit var productoItemPrecio: ArrayList<String>
    private lateinit var productoItemImagen: ArrayList<String>
    private lateinit var productoItemDescripcion: ArrayList<String>
    private lateinit var productoItemTalla: ArrayList<String>
    private lateinit var productoItemCantidad: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId: String
    private lateinit var spinnerMetodoPago: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Incializar Firebase y Usuario Detalles
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()
        //Traer datos del usuario
        traerDatosUsuario()

        //Traer detalles del usuario de firebase
        val intent = intent
        productoItemNombre =
            intent.getStringArrayListExtra("ProductoItemNombre") as ArrayList<String>
        productoItemPrecio =
            intent.getStringArrayListExtra("ProductoItemPrecio") as ArrayList<String>
        productoItemImagen =
            intent.getStringArrayListExtra("ProductoItemImagen") as ArrayList<String>
        productoItemDescripcion =
            intent.getStringArrayListExtra("ProductoItemDescripcion") as ArrayList<String>
        productoItemTalla = intent.getStringArrayListExtra("ProductoItemTalla") as ArrayList<String>
        productoItemCantidad =
            intent.getIntegerArrayListExtra("ProductoItemCantidad") as ArrayList<Int>

        montoTotal = calcularMontoTotal().toString() + "MX$"
        /*binding.tvMontoTotalPago.isEnabled = false*/
        binding.tvMontoTotalPago.setText(montoTotal)


        binding.btnRealizarPedido.setOnClickListener {
            //Obtener datos de los textView
            nombre = binding.tietNombrePago.text.toString().trim()
            direccion = binding.tietDireccionPago.text.toString().trim()
            celular = binding.tietCelularPago.text.toString().trim()
            /*metodoPago = listOf(binding.spinnerMetodoPago.toString().trim()).toString()*/

            if (nombre.isBlank() || direccion.isBlank() || celular.isBlank()) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                realizarOrden()
                crearPDF()
            }
        }

        // Cambiar el color de la barra de estado
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        //Instancia spinner
        spinnerMetodoPago = findViewById(R.id.spinnerMetodoPago)
        //Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configurar adapter para el Spinner
        val metodoPago = listOf("EFECTIVO 💵", "TRANSFERENCIA 💳")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, metodoPago)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMetodoPago.adapter = spinnerAdapter

        spinnerMetodoPago.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                this@PagoActivity.metodoPago = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun crearPDF() {
        val pdfFileName = "Orden_${System.currentTimeMillis()}.pdf"
        val pdfPath = File(getExternalFilesDir(null), pdfFileName)

        try {
            val pdfWriter = PdfWriter(pdfPath)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            // Acceder al logo desde drawable
            val logoBitmap = BitmapFactory.decodeResource(resources, R.drawable.logodos) // 'logo' es el nombre del archivo sin extensión
            val stream = ByteArrayOutputStream()
            logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val logoData = ImageDataFactory.create(stream.toByteArray())

            // Añadir el logo al PDF
            val logo = Image(logoData)
            logo.setHorizontalAlignment(HorizontalAlignment.CENTER)
            logo.setHeight(50f)
            document.add(logo)

            // Agregar título de la orden
            document.add(Paragraph("Detalles de la Orden")
                .setBold()
                .setFontSize(20f)
                .setTextAlignment(TextAlignment.CENTER))

            // Fecha y hora de la orden
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val fechaHora = sdf.format(Date())
            document.add(Paragraph("Fecha y Hora: $fechaHora")
                .setTextAlignment(TextAlignment.RIGHT))

            // Datos del cliente
            document.add(Paragraph("\nDatos del Cliente")
                .setBold()
                .setFontSize(16f))

            val customerInfoTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 2f))).useAllAvailableWidth()
            customerInfoTable.addCell(Cell().add(Paragraph("Nombre:")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            customerInfoTable.addCell(Cell().add(Paragraph(nombre)))
            customerInfoTable.addCell(Cell().add(Paragraph("Dirección:")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            customerInfoTable.addCell(Cell().add(Paragraph(direccion)))
            customerInfoTable.addCell(Cell().add(Paragraph("Celular:")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            customerInfoTable.addCell(Cell().add(Paragraph(celular)))
            customerInfoTable.addCell(Cell().add(Paragraph("Método de Pago:")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            customerInfoTable.addCell(Cell().add(Paragraph(metodoPago)))
            customerInfoTable.addCell(Cell().add(Paragraph("Monto Total:")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            customerInfoTable.addCell(Cell().add(Paragraph(montoTotal)))

            document.add(customerInfoTable)

            // Número de cuenta
            document.add(Paragraph("\nDatos Bancarios:")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold())
            document.add(Paragraph("\nNo. De Cuenta: 1234567899876543"))
            document.add(Paragraph("\nSi tu pago es en efectivo, puedes ignorar esta sección."))

            // Detalles de productos
            document.add(Paragraph("\nProductos:").setBold().setFontSize(16f))

            val productTable = Table(UnitValue.createPercentArray(floatArrayOf(3f, 1f, 1f, 1f)))
                .useAllAvailableWidth()
            productTable.addCell(Cell().add(Paragraph("Producto")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            productTable.addCell(Cell().add(Paragraph("Precio")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            productTable.addCell(Cell().add(Paragraph("Cantidad")).setBackgroundColor(ColorConstants.LIGHT_GRAY))
            productTable.addCell(Cell().add(Paragraph("Talla")).setBackgroundColor(ColorConstants.LIGHT_GRAY))

            for (i in productoItemNombre.indices) {
                productTable.addCell(Cell().add(Paragraph(productoItemNombre[i])))
                productTable.addCell(Cell().add(Paragraph(productoItemPrecio[i])))
                productTable.addCell(Cell().add(Paragraph(productoItemCantidad[i].toString())))
                productTable.addCell(Cell().add(Paragraph(productoItemTalla[i])))
            }

            document.add(productTable)

            document.close()

            // PDF generado, lanzar notificación
            lanzarNotificacion(pdfFileName, pdfPath)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al crear el PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun lanzarNotificacion(pdfFileName: String, pdfPath: File) {
        val notificationId = 1

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Configurar el canal de notificación (para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "orden_channel",
                "Ordenes",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir el PDF
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        val pdfUri = FileProvider.getUriForFile(this, "$packageName.provider", pdfPath)
        pdfIntent.setDataAndType(pdfUri, "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            pdfIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construir la notificación
        val notificationBuilder = NotificationCompat.Builder(this, "orden_channel")
            .setSmallIcon(R.drawable.logodos) // Debes tener un ícono de PDF en tus recursos
            .setContentTitle("Tu Orden Orden ha sido Generada.")
            .setContentText("Tu archivo para el pago esta listo. $pdfFileName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Lanzar la notificación
        notificationManager.notify(notificationId, notificationBuilder.build())
    }


    private fun realizarOrden() {
        userId = auth.currentUser?.uid ?: ""
        val tiempo = System.currentTimeMillis()
        val itemPushLlave = databaseReference.child("DetallesOrden").push().key
        val detalllesOrden = DetallesOrden(
            userId,
            nombre,
            productoItemNombre,
            productoItemPrecio,
            productoItemTalla,
            productoItemImagen,
            productoItemCantidad,
            direccion,
            montoTotal,
            metodoPago,
            celular,
            tiempo,
            itemPushLlave,
            false,
            false,
        )
        val ordenReference = databaseReference.child("DetallesOrden").child(itemPushLlave!!)
        ordenReference.setValue(detalllesOrden).addOnSuccessListener {
            val bottomSheetDialog = CumplidoBottomSheetFragment()
            bottomSheetDialog.show(supportFragmentManager, "TEST")
            removerItemDelCarrito()
            agregarOrdenAlHistorial(detalllesOrden)
        }.addOnFailureListener {
            Toast.makeText(
                this,
                "Error al hacer tu pedido. Intenta Nuevamente.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun agregarOrdenAlHistorial(detalllesOrden: DetallesOrden) {
        databaseReference.child("Usuarios").child(userId).child("HistorialCompra")
            .child(detalllesOrden.itemPushLlave!!)
            .setValue(detalllesOrden).addOnSuccessListener {

            }


    }

    private fun removerItemDelCarrito() {
        val carritoItemsReferencia =
            databaseReference.child("Usuarios").child(userId).child("CarritoItems")
        carritoItemsReferencia.removeValue()
    }

    private fun calcularMontoTotal(): Int {
        var montoTotal = 0
        for (i in 0 until productoItemPrecio.size) {
            var precio = productoItemPrecio[i]
            val lastChar = precio.last()
            val precioIntValor = if (lastChar == '$') {
                precio.dropLast(1).toInt()
            } else {
                precio.toInt()
            }
            var cantidad = productoItemCantidad[i]
            montoTotal += precioIntValor * cantidad
        }
        return montoTotal
    }

    private fun traerDatosUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            val usuarioId = usuario.uid
            val usuarioRef = databaseReference.child("Usuarios").child(usuarioId)

            usuarioRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val nombres = snapshot.child("nombre").getValue(String::class.java) ?: ""
                        val direcciones =
                            snapshot.child("direccion").getValue(String::class.java) ?: ""
                        val celulares = snapshot.child("celular").getValue(String::class.java) ?: ""

                        binding.apply {
                            tietNombrePago.setText(nombres)
                            tietDireccionPago.setText(direcciones)
                            tietCelularPago.setText(celulares)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
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