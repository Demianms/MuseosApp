package com.demian.chamus.models

import com.google.gson.annotations.SerializedName

data class Room(
    val id: Int,
    val nombre: String,
    val imagen: String?, // 'imagen' en JSON -> 'imagen' en Kotlin. Puede ser nulo.
    val descripcion: String,

    @SerializedName("creado")
    val fechaCreacion: String,

    @SerializedName("actualizado")
    val fechaActualizacion: String
) {
    // Propiedad calculada para hacer más fácil el uso de la imagen en Composables
    val imageUrl: String?
        get() = imagen
}