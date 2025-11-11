package me.santiagobrito.parkup

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object ParkingRepository {

    private const val COLLECTION = "parkings"
    private val db = Firebase.firestore

    fun addParking(spot: ParkingSpot, onResult: (Boolean) -> Unit) {
        // Guardamos como mapa simple
        val data = hashMapOf(
            "name" to spot.name,
            "address" to spot.address,
            "pricePerHour" to spot.pricePerHour,
            "openingTime" to spot.openingTime,
            "closingTime" to spot.closingTime,
            "latitude" to spot.latitude,
            "longitude" to spot.longitude
        )

        db.collection(COLLECTION)
            .add(data)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun listenParkings(onChange: (List<ParkingSpot>) -> Unit) {
        db.collection(COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    onChange(emptyList())
                    return@addSnapshotListener
                }

                val list = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: ""
                    val address = doc.getString("address") ?: return@mapNotNull null
                    val price = doc.getDouble("pricePerHour") ?: 0.0
                    val opening = doc.getString("openingTime") ?: ""
                    val closing = doc.getString("closingTime") ?: ""
                    val lat = doc.getDouble("latitude") ?: return@mapNotNull null
                    val lng = doc.getDouble("longitude") ?: return@mapNotNull null

                    ParkingSpot(
                        id = doc.id,
                        name = name,
                        address = address,
                        pricePerHour = price,
                        openingTime = opening,
                        closingTime = closing,
                        latitude = lat,
                        longitude = lng
                    )
                }

                onChange(list)
            }
    }
}
