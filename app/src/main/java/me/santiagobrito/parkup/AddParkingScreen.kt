package me.santiagobrito.parkup

import android.location.Geocoder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun AddParkingScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var pricePerHour by remember { mutableStateOf("") }
    var openingTime by remember { mutableStateOf("") }
    var closingTime by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Agregar parqueadero")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pricePerHour,
            onValueChange = { pricePerHour = it },
            label = { Text("Valor por hora") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = openingTime,
            onValueChange = { openingTime = it },
            label = { Text("Hora de apertura (ej: 08:00)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = closingTime,
            onValueChange = { closingTime = it },
            label = { Text("Hora de cierre (ej: 20:00)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Text(text = error ?: "", color = Color.Red)
        }

        Spacer(modifier = Modifier.padding(top = 4.dp))

        Button(
            onClick = {
                if (address.isBlank() || pricePerHour.isBlank()) {
                    error = "Dirección y precio son obligatorios"
                    return@Button
                }

                loading = true
                error = null

                scope.launch {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val result = geocoder.getFromLocationName(address, 1)
                        val loc = result?.firstOrNull()

                        if (loc == null) {
                            error = "No se pudo obtener ubicación de esa dirección"
                            loading = false
                            return@launch
                        }

                        val spot = ParkingSpot(
                            name = name,
                            address = address,
                            pricePerHour = pricePerHour.toDoubleOrNull() ?: 0.0,
                            openingTime = openingTime,
                            closingTime = closingTime,
                            latitude = loc.latitude,
                            longitude = loc.longitude
                        )

                        ParkingRepository.addParking(spot) { ok ->
                            loading = false
                            if (ok) {
                                onSaved()
                            } else {
                                error = "Error al guardar. Intenta de nuevo."
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        error = "Error al geocodificar la dirección"
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Guardando..." else "Guardar parqueadero")
        }

        Button(
            onClick = onBack,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
