package me.santiagobrito.parkup

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class BookingRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val MIN_ADVANCE_MIN = 30L // margen para cancelar (> 30 min)

    private fun requireUid(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Debes iniciar sesión")

    /** Crear reserva guardando el id del documento dentro del objeto */
    suspend fun create(parkingId: String, startAt: Long, endAt: Long) {
        val uid = requireUid()
        val ref = db.collection("bookings").document()
        val booking = Booking(
            id = ref.id,
            userId = uid,
            parkingId = parkingId,
            startAt = startAt,
            endAt = endAt,
            status = "reserved",
            createdAt = System.currentTimeMillis()
        )
        ref.set(booking).await()
    }

    // Activa esta bandera en true cuando crees el índice compuesto (userId + startAt) en Firestore
    private val USE_ORDERED_QUERY = false

    /** Flujo en tiempo real de mis reservas */
    fun listenMyBookings(): Flow<List<Booking>> =
        if (USE_ORDERED_QUERY) listenMyBookingsOrdered() else listenMyBookingsSimple()

    // Sin orderBy (no requiere índice) — útil para probar ya
    private fun listenMyBookingsSimple(): Flow<List<Booking>> = callbackFlow {
        val uid = requireUid()
        val reg = db.collection("bookings")
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snap.documents.mapNotNull { d ->
                    d.toObject(Booking::class.java)?.copy(id = d.id)
                }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    // Con orderBy por fecha (requiere índice compuesto en Firestore)
    private fun listenMyBookingsOrdered(): Flow<List<Booking>> = callbackFlow {
        val uid = requireUid()
        val reg = db.collection("bookings")
            .whereEqualTo("userId", uid)
            .orderBy("startAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) { trySend(emptyList()); return@addSnapshotListener }
                val list = snap.documents.mapNotNull { d ->
                    d.toObject(Booking::class.java)?.copy(id = d.id)
                }
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    /** Cancela si falta > 30 min y la reserva está en "reserved" (transacción) */
    suspend fun cancelBooking(id: String) {
        db.runTransaction { tx ->
            val ref = db.collection("bookings").document(id)
            val snap = tx.get(ref)
            if (!snap.exists()) error("No existe la reserva.")
            val b = snap.toObject(Booking::class.java) ?: error("Datos inválidos.")

            val now = System.currentTimeMillis()
            val canCancel = b.status == "reserved" &&
                    now < b.startAt - TimeUnit.MINUTES.toMillis(MIN_ADVANCE_MIN)

            if (!canCancel) error("No se puede cancelar (menos de $MIN_ADVANCE_MIN min).")

            tx.update(ref, mapOf("status" to "cancelled", "cancelledAt" to now))
            null
        }.await()
    }
}


