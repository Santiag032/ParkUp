package me.santiagobrito.parkup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(parkingId: String = "") {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { BookingRepository() }

    val BrandBlue = Blue

    val todayMillis = remember { Instant.now().toEpochMilli() }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)
    val timeState = rememberTimePickerState(is24Hour = true, initialHour = 14, initialMinute = 0)
    var showTime by remember { mutableStateOf(false) }

    val snackbar = remember { SnackbarHostState() }

    // Estado para "Mis reservas"
    var showMyBookings by remember { mutableStateOf(false) }
    val bookings by repo.listenMyBookings().collectAsState(initial = emptyList())

    fun canCancel(b: Booking, minAdvanceMin: Long = 30L): Boolean {
        val now = System.currentTimeMillis()
        return b.status == "reserved" && now < b.startAt - (minAdvanceMin * 60_000)
    }

    val scheme = MaterialTheme.colorScheme.copy(
        primary = BrandBlue,
        primaryContainer = BrandBlue,
        onPrimary = Color.White
    )

    MaterialTheme(colorScheme = scheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Calendario") },
                    actions = {
                        TextButton(onClick = { showMyBookings = true }) {
                            Text("Mis reservas")
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbar) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)              // respeta top/bottom bars
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    "Programa tus próximas visitas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // === DatePicker con cabecera visible pero compactado ===
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer(
                                    scaleX = 0.90f,   // ajusta 0.88–0.94 si quieres más/menos compacto
                                    scaleY = 0.90f
                                )
                        ) {
                            DatePicker(
                                state = dateState,
                                // mantenemos title/headline por defecto (se ve la fecha arriba)
                                showModeToggle = false,              // quita el switch de modos para ahorrar altura
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        OutlinedButton(
                            onClick = { showTime = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(String.format("%02d:%02d", timeState.hour, timeState.minute))
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                val canReserve = dateState.selectedDateMillis != null
                Button(
                    enabled = canReserve,
                    onClick = {
                        val millis = dateState.selectedDateMillis ?: return@Button
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        val selectedTime = LocalTime.of(timeState.hour, timeState.minute)

                        val start = ZonedDateTime.of(
                            selectedDate, selectedTime, ZoneId.systemDefault()
                        ).toInstant().toEpochMilli()
                        val end = start + 60 * 60 * 1000L // 1h

                        scope.launch {
                            try {
                                repo.create(parkingId = parkingId, startAt = start, endAt = end)
                                snackbar.showSnackbar("Tu reserva se creó correctamente")
                            } catch (e: Exception) {
                                Toast.makeText(ctx, e.message ?: "Error al reservar", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandBlue,
                        contentColor = Color.White
                    )
                ) { Text("Reservar cita") }

                // margen inferior para que nunca lo tape la bottom bar
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }

    // TimePicker en diálogo
    if (showTime) {
        Dialog(onDismissRequest = { showTime = false }) {
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    TimePicker(state = timeState)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showTime = false }) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { showTime = false }) { Text("OK") }
                    }
                }
            }
        }
    }

    // Diálogo "Mis reservas" con cancelar
    if (showMyBookings) {
        Dialog(onDismissRequest = { showMyBookings = false }) {
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Mis reservas", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    if (bookings.isEmpty()) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) { Text("Aún no tienes reservas") }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp)
                        ) {
                            items(bookings, key = { it.id }) { b ->
                                val sdf = remember { SimpleDateFormat("EEE d MMM, HH:mm", Locale.getDefault()) }
                                val start = sdf.format(Date(b.startAt))
                                val end = sdf.format(Date(b.endAt))
                                val cancellable = canCancel(b)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("Parqueadero: ${b.parkingId}")
                                        Text("Inicio: $start")
                                        Text("Fin: $end")
                                        Text("Estado: ${b.status}")
                                        Spacer(Modifier.height(8.dp))
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                            Button(
                                                enabled = cancellable,
                                                onClick = {
                                                    scope.launch {
                                                        try {
                                                            repo.cancelBooking(b.id)
                                                            snackbar.showSnackbar("Reserva cancelada")
                                                        } catch (e: Exception) {
                                                            snackbar.showSnackbar(e.message ?: "No se pudo cancelar")
                                                        }
                                                    }
                                                },
                                                shape = RoundedCornerShape(10.dp)
                                            ) { Text("Cancelar reserva") }
                                        }
                                        if (!cancellable && b.status == "reserved") {
                                            Spacer(Modifier.height(4.dp))
                                            Text(
                                                "No cancelable (menos de 30 min para iniciar).",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF666666)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showMyBookings = false }) { Text("Cerrar") }
                    }
                }
            }
        }
    }
}







