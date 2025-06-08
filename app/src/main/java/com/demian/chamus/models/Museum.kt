package com.demian.chamus.models

import com.google.gson.annotations.SerializedName

data class Museum(
    val id: Int,
    val nombre: String,
    val imagen: String?,

    @SerializedName("hora_de_apertura")
    val horaDeApertura: String,

    @SerializedName("hora_de_cierre")
    val horaDeCierre: String,

    val latitud: Double,
    val longitud: Double,
    val descripcion: String,
    val precio: Double,
    val url: String?,

    @SerializedName("numero_de_salas")
    val numeroDeSalas: Int,

    val estado: String,

    @SerializedName("creado")
    val fechaCreacion: String,

    @SerializedName("actualizado")
    val fechaActualizacion: String,

    // ¡NUEVO CAMPO! Lista de objetos Room
    val rooms: List<Room> // Asegúrate de que el nombre 'rooms' coincida con el JSON
) {
    // Propiedad calculada para el estado activo/inactivo (para tu MuseumCard)
    val isActive: Boolean
        get() = estado.equals("active", ignoreCase = true)

    // Propiedad calculada para la URL de la imagen principal del museo
    val imageUrl: String?
        get() = imagen
}