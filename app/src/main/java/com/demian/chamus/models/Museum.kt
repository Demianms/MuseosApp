package com.demian.chamus.models

import com.google.gson.annotations.SerializedName

data class Museum(
    val id: Int,
    @SerializedName("nombre") val name: String,
    @SerializedName("imagen") val imageUrl: String?,
    @SerializedName("hora_de_apertura") val openingTime: String,
    @SerializedName("hora_de_cierre") val closingTime: String,
    val latitud: Double,
    val longitud: Double,
    val descripcion: String,
    val precio: Double,
    val url: String,
    @SerializedName("numero_de_salas") val numberOfRooms: Int,
    val estado: String,
    val creado: String,
    val actualizado: String,
    @SerializedName("descuento") val descuento: String,
    val rooms: List<Room>
)

data class Room(
    val id: Int,
    @SerializedName("nombre") val name: String,
    @SerializedName("imagen") val imageUrl: String?,
    val descripcion: String,
    val creado: String,
    val actualizado: String
)