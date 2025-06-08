package com.demian.chamus.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://chamus.restteach.com" // ¡IMPORTANTE! Lee la nota abajo
    // Si usas tu IP local del equipo (ej. 192.168.1.X) asegúrate que ambos estén en la misma red
    // Si estás en un emulador, 10.0.2.2 apunta a tu localhost.
    // Si estás en un dispositivo físico, necesitas la IP local de tu máquina donde corre Laravel.

    val instance: MuseumApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MuseumApiService::class.java)
    }
}