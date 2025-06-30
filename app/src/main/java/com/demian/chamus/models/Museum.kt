import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

// --- Clase para una sola Sala (Room) ---
@Serializable
data class Room(
    val id: Int,
    val nombre: String,
    val imagen: String,
    val descripcion: String,
    val creado: String,
    val actualizado: String
)

// --- Clase para el Museo completo ---
@Serializable
data class Museum(
    val id: Int,
    val nombre: String,
    val imagen: String,
    @SerialName("hora_de_apertura") val horaDeApertura: String,
    @SerialName("hora_de_cierre") val horaDeCierre: String,
    val latitud: Double?,
    val longitud: Double?,
    val descripcion: String,
    val precio: Double,
    val url: String,
    @SerialName("numero_de_salas") val numeroDeSalas: Int,
    val estado: String,
    val creado: String,
    val actualizado: String,
    val rooms: List<Room> // ¡Aquí también! Una lista de objetos Room.
)