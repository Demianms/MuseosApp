package com.demian.chamus.models
import com.google.gson.annotations.SerializedName

data class Museum(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val latitud: Double?,
    val longitud: Double?,
    @SerializedName("url") val url: String?,
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

data class CotizacionGrupalRequest(
    val museum_id: Int,
    val appointment_date: String, // "YYYY-MM-DD"
    val start_hour: String,       // "HH:MM:SS"
    val end_hour: String,         // "HH:MM:SS"
    val total_people: Int,
    val total_people_discount: Int,
    @SerializedName("total_people_whitout_discount")
    val totalPeopleWithoutDiscount: Int,
    val total_infants: Int,
    @SerializedName("total_whit_discount")
    val totalWithDiscount: Double,
    @SerializedName("total_whitout_discount")
    val totalWithoutDiscount: Double,
    val price_total: Double
)

data class CotizacionGrupalResponse(
    val message: String,
    val unique_id: String,
    val cotizacion: CotizacionDetalle?
)


data class CotizacionDetalle(
    val id: Int,
    val unique_id: String,
    val museum_id: Int,
    @SerializedName("museum")
    val museum: MuseumBrief?,
    val appointment_date: String,
    val start_hour: String,
    val end_hour: String,
    val total_people: Int,
    val total_people_discount: Int,
    @SerializedName("total_people_whitout_discount")
    val totalPeopleWithoutDiscount: Int,
    val total_infants: Int,
    @SerializedName("total_whit_discount")
    val totalWithDiscount: Double,
    @SerializedName("total_whitout_discount")
    val totalWithoutDiscount: Double,
    val price_total: Double,
    val status: String,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class MuseumBrief(
    val id: Int,
    val nombre: String
)

data class DiscountedPeopleGroup(
    var count: String = "0", // Cantidad de personas en este grupo
    var discount: Discount? = null // El descuento aplicado a este grupo
)