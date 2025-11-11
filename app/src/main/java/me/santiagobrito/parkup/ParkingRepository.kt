package me.santiagobrito.parkup

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase as KtxFirebase // para firestore()

object ParkingRepository {

    private val auth = Firebase.auth
    private val db = KtxFirebase.firestore

    // Guarda en /parkings/{uid}/parkings/{autoId}
    fun addParking(parking: ParkingSpot, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(false)
            return
        }

        db.collection("parkings")
            .document(user.uid)
            .collection("parkings")
            .add(parking)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    // Lee TODOS los documentos de cualquier subcolección "parkings"
    fun listenAllParkings(onChange: (List<ParkingSpot>) -> Unit): ListenerRegistration {
        return db.collectionGroup("parkings")
            .addSnapshotListener { snap, e ->
                if (e != null || snap == null) {
                    onChange(emptyList())
                    return@addSnapshotListener
                }

                val list = snap.documents.mapNotNull { doc ->
                    doc.toObject(ParkingSpot::class.java)?.copy(id = doc.id)
                }

                onChange(list)
            }
    }
}

