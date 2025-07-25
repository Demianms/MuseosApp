package com.demian.chamus.models
import com.google.gson.annotations.SerializedName

data class Museum(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val latitud: Double?,
    val longitud: Double?,
    @SerializedName("url_web") val url: String?,
    val imagen: String,
    val estado: String,
    @SerializedName("hora_de_apertura") val horaDeApertura: String? = null,
    @SerializedName("hora_de_cierre") val horaDeCierre: String? = null,
    val precio: String,
    val categories: List<Category>,
    @SerializedName("descuentos_asociados") val descuentosAsociados: List<Discount>? = null,
    val rooms: List<Room>,
    @SerializedName("creado") val creado: String? = null,
    @SerializedName("actualizado") val actualizado: String? = null,
    @SerializedName("numero_de_salas") val numeroDeSalas: Int? = null
)

data class Discount(
    val id: Int,
    @SerializedName("valor_descuento") val valorDescuento: String,
    @SerializedName("descripcion_aplicacion") val descripcionAplicacion: String? = null
)

data class Room(
    val id: Int,
    val nombre: String,
    val imagen: String,
    val descripcion: String,
    @SerializedName("creado") val creado: String, // Si "creado" en JSON es snake_case
    @SerializedName("actualizado") val actualizado: String // Si "actualizado" en JSON es snake_case
)

data class Category(
    val id: Int,
    val nombre: String
)