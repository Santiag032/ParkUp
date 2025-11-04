@file:OptIn(ExperimentalMaterial3Api::class)

package me.santiagobrito.parkup

import android.Manifest
import android.location.Geocoder
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import me.santiagobrito.parkup.DirectionsClient
import java.util.Locale

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    mapsApiKey: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasLocation by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var destinoTexto by remember { mutableStateOf("") }
    var destinoLatLng by remember { mutableStateOf<LatLng?>(null) }
    var ruta by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.1193, -73.1227), 12f) // Bucaramanga por defecto
    }

    // --- Permisos de ubicación ---
    val permisos = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        hasLocation = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        // Si se conceden, centra en la ubicación actual
        if (hasLocation) {
            scope.launch {
                val loc = fused.lastLocation.await()
                loc?.let {
                    myLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(myLocation!!, 15f),
                        800
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) { permisosLauncher.launch(permisos) }

    // --- Buscar dirección, obtener ruta y ajustar cámara ---
    fun geocodificarYRuta() {
        scope.launch {
            val origen = myLocation ?: run {
                permisosLauncher.launch(permisos); return@launch
            }
            // Geocoder simple (sin Places)
            val geocoder = Geocoder(context, Locale.getDefault())
            val dest = try {
                val list = if (Build.VERSION.SDK_INT >= 33) {
                    geocoder.getFromLocationName(destinoTexto, 1)
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(destinoTexto, 1)
                }
                list?.firstOrNull()?.let { LatLng(it.latitude, it.longitude) }
            } catch (_: Exception) { null }

            if (dest == null) {
                // Aquí podrías mostrar un Snackbar de “No se encontró la dirección”
                return@launch
            }

            destinoLatLng = dest

            val result = DirectionsClient.fetchRoute(
                origin = origen,
                destination = dest,
                apiKey = mapsApiKey
            )
            ruta = result?.path ?: emptyList()

            // Ajustar cámara para ver origen + ruta + destino
            val builder = LatLngBounds.Builder().include(origen).include(dest)
            ruta.forEach { builder.include(it) }
            val bounds = try { builder.build() } catch (_: Exception) { null }

            if (bounds != null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngBounds(bounds, 120),
                    800
                )
            } else {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(dest, 14f),
                    600
                )
            }
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = destinoTexto,
                onValueChange = { destinoTexto = it },
                label = { Text("¿A dónde vamos?") },
                placeholder = { Text("Escribe una dirección o lugar") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { geocodificarYRuta() }
                )
            )

            Button(
                onClick = { geocodificarYRuta() },
                enabled = destinoTexto.isNotBlank()
            ) { Text("Ir") }
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocation),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocation,
                zoomControlsEnabled = false
            )
        ) {
            myLocation?.let {
                Marker(state = MarkerState(position = it), title = "Mi ubicación")
            }
            destinoLatLng?.let {
                Marker(state = MarkerState(position = it), title = "Destino")
            }
            if (ruta.isNotEmpty()) {
                Polyline(points = ruta, width = 10f)
            }
        }
    }
}
