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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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


@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    mapsApiKey: String,
    onAddParking: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var hasLocation by remember { mutableStateOf(false) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var searchText by remember { mutableStateOf("") }
    var destinoLatLng by remember { mutableStateOf<LatLng?>(null) }
    var ruta by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    // 👇 lista de parqueaderos desde Firestore
    var parkings by remember { mutableStateOf<List<ParkingSpot>>(emptyList()) }

    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.1193, -73.1227), 12f)
    }

    // --------- Escuchar Firestore ----------
    DisposableEffect(Unit) {
        val reg = ParkingRepository.listenAllParkings { spots ->
            parkings = spots
        }
        onDispose { reg.remove() }
    }

    // --------- Permisos ubicación (igual que antes) ----------
    val permisos = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        hasLocation = granted[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                granted[Manifest.permission.ACCESS_COARSE_LOCATION] == true

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

    LaunchedEffect(Unit) {
        permisosLauncher.launch(permisos)
    }

    // --------- Buscar parqueadero por nombre/dirección ----------
    fun buscarParqueadero() {
        val q = searchText.trim()
        if (q.isEmpty()) return

        val match = parkings.firstOrNull {
            it.name.contains(q, ignoreCase = true) ||
                    it.address.contains(q, ignoreCase = true)
        }

        match?.let {
            val target = LatLng(it.latitude, it.longitude)
            destinoLatLng = target
            ruta = emptyList() // si no quieres ruta aquí
            scope.launch {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(target, 17f),
                    800
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Buscador
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Buscar parqueaderos") },
                placeholder = { Text("Nombre o dirección") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { buscarParqueadero() }
                )
            )

            Button(
                onClick = { buscarParqueadero() },
                enabled = searchText.isNotBlank()
            ) {
                Text("Buscar")
            }
        }

        // Botón agregar parqueadero
        Button(
            onClick = onAddParking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text("Agregar parqueadero")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mapa
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocation),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocation,
                zoomControlsEnabled = false
            )
        ) {
            // Mi ubicación
            myLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Mi ubicación"
                )
            }

            // Destino/búsqueda (opcional)
            destinoLatLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Seleccionado"
                )
            }

            // 👇 Todos los parqueaderos desde Firestore
            parkings.forEach { parking ->
                if (parking.latitude != 0.0 && parking.longitude != 0.0) {
                    val pos = LatLng(parking.latitude, parking.longitude)
                    Marker(
                        state = MarkerState(position = pos),
                        title = parking.name,
                        snippet = "${parking.address}\n$${parking.pricePerHour}/hora",
                        icon = parkingMarkerIcon(context) // tu icono personalizado
                    )
                }
            }
        }
    }
}



