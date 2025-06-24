package com.demian.chamus.models

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val country: String
)

data class Current(
    val temp_c: Float,
    val condition: Condition,
    val humidity: Int,
    val wind_kph: Float
)

data class Condition(
    val text: String,
    val icon: String
)