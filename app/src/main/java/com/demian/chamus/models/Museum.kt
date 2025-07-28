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
    val message: String? = null,
    val unique_id: String? = null,
    val cotizacion: CotizacionDetalle? = null,

    // Campos directos de la respuesta API
    val id: Int? = null,
    val museum_id: Int? = null,
    val appointment_date: String? = null,
    val start_hour: String? = null,
    val end_hour: String? = null,
    val total_people: Int? = null,
    val total_people_discount: Int? = null,
    @SerializedName("total_people_whitout_discount")
    val totalPeopleWithoutDiscount: Int? = null,
    val total_infants: Int? = null,
    @SerializedName("total_whit_discount")
    val totalWithDiscount: String? = null,
    @SerializedName("total_whitout_discount")
    val totalWithoutDiscount: String? = null,
    val price_total: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val museum: Museum? = null,
    @SerializedName("museum_name")
    val museumName: String? = null
) {
    fun getEffectiveCotizacion(): CotizacionDetalle? {
        return cotizacion ?: if (id != null) {
            CotizacionDetalle(
                id = id,
                unique_id = unique_id ?: "",
                museum_id = museum_id ?: 0,
                museum = museum?.let {
                    MuseumBrief(
                        id = it.id,
                        nombre = it.nombre
                    )
                },
                appointment_date = appointment_date ?: "",
                start_hour = start_hour ?: "",
                end_hour = end_hour ?: "",
                total_people = total_people ?: 0,
                total_people_discount = total_people_discount ?: 0,
                totalPeopleWithoutDiscount = totalPeopleWithoutDiscount ?: 0,
                total_infants = total_infants ?: 0,
                totalWithDiscount = totalWithDiscount?.toDoubleOrNull() ?: 0.0,
                totalWithoutDiscount = totalWithoutDiscount?.toDoubleOrNull() ?: 0.0,
                price_total = price_total?.toDoubleOrNull() ?: 0.0,
                status = "active",
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        } else {
            null
        }
    }
}

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
    val nombre: String? = "Museo"
)

data class DiscountedPeopleGroup(
    var count: String = "0", // Cantidad de personas en este grupo
    var discount: Discount? = null // El descuento aplicado a este grupo
)