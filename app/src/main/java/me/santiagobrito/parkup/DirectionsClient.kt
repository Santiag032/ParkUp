package me.santiagobrito.parkup

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject

object DirectionsClient {

    private val httpClient by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        OkHttpClient.Builder()
            .addInterceptor(log)
            .build()
    }

    /**
     * Llama a la Directions API (driving) y devuelve la polilínea decodificada
     * junto con bounds aproximados para ajustar cámara.
     */
    suspend fun fetchRoute(
        origin: LatLng,
        destination: LatLng,
        apiKey: String
    ): RouteResult? = withContext(Dispatchers.IO) {
        val url = Uri.Builder()
            .scheme("https")
            .authority("maps.googleapis.com")
            .path("maps/api/directions/json")
            .appendQueryParameter("origin", "${origin.latitude},${origin.longitude}")
            .appendQueryParameter("destination", "${destination.latitude},${destination.longitude}")
            .appendQueryParameter("mode", "driving")
            .appendQueryParameter("key", apiKey)
            .build()
            .toString()

        val request = Request.Builder().url(url).get().build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return@withContext null

        val body = response.body?.string() ?: return@withContext null
        val json = JSONObject(body)

        val routes = json.optJSONArray("routes") ?: return@withContext null
        if (routes.length() == 0) return@withContext null

        val route0 = routes.getJSONObject(0)
        val overview = route0.optJSONObject("overview_polyline")
        val points = overview?.optString("points").orEmpty()
        if (points.isBlank()) return@withContext null

        val decoded = PolyUtil.decode(points) // List<LatLng>
        RouteResult(decoded)
    }

    data class RouteResult(
        val path: List<LatLng>
    )
}