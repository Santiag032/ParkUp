package me.santiagobrito.parkup

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import me.santiagobrito.parkup.R

fun parkingMarkerIcon(
    context: Context,
    size: Int = 96
): BitmapDescriptor {
    // Usa tu drawable ic_parqueadero
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_parqueadero)
        ?: return BitmapDescriptorFactory.defaultMarker() // fallback seguro

    val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)
    return BitmapDescriptorFactory.fromBitmap(scaled)
}
