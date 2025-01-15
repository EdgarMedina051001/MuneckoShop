package com.example.muneckoshop.Model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.ArrayList

class DetallesOrden(): Serializable {
    var usuarioUid: String? = null
    var usuarioNombre: String?= null
    var productoNombres : MutableList<String>? = null
    var productoImagenes : MutableList<String>? = null
    var productoPrecios : MutableList<String>? = null
    var productoTallas : MutableList<String>? = null
    var productoCantidades: MutableList<Int>? = null
    var direccion: String? = null
    var precioTotal: String? = null
    var celular: String? = null
    var metodosPagos: String? = null
    var ordenAceptada: Boolean = false
    var pagoRecibido: Boolean = false
    var itemPushLlave: String? = null
    var tiempoActual: Long = 0

    constructor(parcel: Parcel) : this() {
        usuarioUid = parcel.readString()
        usuarioNombre = parcel.readString()
        direccion = parcel.readString()
        precioTotal = parcel.readString()
        celular = parcel.readString()
        metodosPagos = parcel.readString()
        ordenAceptada = parcel.readByte() != 0.toByte()
        pagoRecibido = parcel.readByte() != 0.toByte()
        itemPushLlave = parcel.readString()
        tiempoActual = parcel.readLong()
    }

    constructor(
        userId: String,
        nombre: String,
        productoItemNombre: ArrayList<String>,
        productoItemPrecio: ArrayList<String>,
        productoItemTalla: ArrayList<String>,
        productoItemImagen: ArrayList<String>,
        productoItemCantidad: ArrayList<Int>,
        direccion: String,
        montoTotal: String,
        metodoPago: String,
        celular: String,
        tiempo: Long,
        itemPushLlave: String?,
        b: Boolean,
        b1: Boolean
    ) : this(){
        this.usuarioUid = userId
        this.usuarioNombre = nombre
        this.productoNombres = productoItemNombre
        this.productoPrecios = productoItemPrecio
        this.productoTallas = productoItemTalla
        this.productoImagenes = productoItemImagen
        this.productoCantidades = productoItemCantidad
        this.direccion = direccion
        this.metodosPagos = metodoPago.toString()
        this.precioTotal = montoTotal
        this.celular = celular
        this.tiempoActual = tiempo
        this.itemPushLlave = itemPushLlave
        this.ordenAceptada = ordenAceptada
        this.pagoRecibido = pagoRecibido
    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(usuarioUid)
        parcel.writeString(usuarioNombre)
        parcel.writeString(direccion)
        parcel.writeString(precioTotal)
        parcel.writeString(celular)
        parcel.writeString(metodosPagos)
        parcel.writeByte(if (ordenAceptada) 1 else 0)
        parcel.writeByte(if (pagoRecibido) 1 else 0)
        parcel.writeString(itemPushLlave)
        parcel.writeLong(tiempoActual)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DetallesOrden> {
        override fun createFromParcel(parcel: Parcel): DetallesOrden {
            return DetallesOrden(parcel)
        }

        override fun newArray(size: Int): Array<DetallesOrden?> {
            return arrayOfNulls(size)
        }
    }
}