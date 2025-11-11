package me.santiagobrito.parkup


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class BookingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun uid(): String = auth.currentUser?.uid ?: ""

    suspend fun create(parkingId: String, startAt: Long, endAt: Long) {
        val booking = Booking(userId = uid(), parkingId = parkingId, startAt = startAt, endAt = endAt)
        db.collection("bookings").add(booking).await()
    }

    fun userQuery() = db.collection("bookings")
        .whereEqualTo("userId", uid())
        .orderBy("startAt", Query.Direction.DESCENDING)
}
