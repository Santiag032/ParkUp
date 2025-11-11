@file:Suppress("MissingPermission")

package me.santiagobrito.parkup

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
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

    // parqueaderos desde Firestore
    var parkings by remember { mutableStateOf<List<ParkingSpot>>(emptyList()) }

    // búsqueda
    var search by remember { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }

    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(7.1193, -73.1227), 12f)
    }

    // Permisos ubicación
    val permisosLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        hasLocation =
            granted[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
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
        permisosLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Escuchar parqueaderos
    LaunchedEffect(Unit) {
        ParkingRepository.listenParkings { list ->
            parkings = list
        }
    }

    val filtered = remember(search, parkings) {
        if (search.isBlank()) parkings
        else parkings.filter {
            (it.name + " " + it.address)
                .contains(search.trim(), ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Buscar parqueaderos
        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
                showResults = true
            },
            label = { Text("Buscar parqueaderos") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            singleLine = true
        )

        // Lista de resultados (simple, debajo del buscador)
        if (showResults && filtered.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Column {
                    filtered.forEach { spot ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showResults = false
                                    search = spot.name.ifBlank { spot.address }

                                    val target = LatLng(spot.latitude, spot.longitude)
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(target, 17f),
                                            600
                                        )
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            Text(text = spot.name.ifBlank { spot.address })
                            if (spot.name.isNotBlank()) {
                                Text(text = spot.address)
                            }
                        }
                    }
                }
            }
        }

        // Botón agregar parqueadero
        Button(
            onClick = onAddParking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text("Agregar parqueadero")
        }

        // Mapa con marcadores
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = hasLocation),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = hasLocation,
                zoomControlsEnabled = false
            )
        ) {
            myLocation?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Mi ubicación"
                )
            }

            parkings.forEach { spot ->
                if (spot.latitude != 0.0 || spot.longitude != 0.0) {
                    Marker(
                        state = MarkerState(LatLng(spot.latitude, spot.longitude)),
                        title = spot.name.ifBlank { "Parqueadero" },
                        snippet = spot.address
                    )
                }
            }
        }
    }
}

