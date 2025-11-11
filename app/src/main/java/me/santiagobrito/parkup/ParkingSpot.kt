package me.santiagobrito.parkup

data class ParkingSpot(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val pricePerHour: Double = 0.0,
    val openingTime: String = "",
    val closingTime: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,

)
