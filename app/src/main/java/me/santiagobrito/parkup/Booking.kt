package me.santiagobrito.parkup


data class Booking(
    val id: String = "",
    val userId: String = "",
    val parkingId: String = "",
    val startAt: Long = 0L,
    val endAt: Long = 0L,
    val status: String = "reserved"
)
