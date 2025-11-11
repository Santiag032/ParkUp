package me.santiagobrito.parkup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import java.time.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(parkingId: String = "") {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { BookingRepository() }


    val BrandBlue = Blue


    val todayMillis = remember { Instant.now().toEpochMilli() }
    val dateState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)

    val timeState = rememberTimePickerState(
        is24Hour = true, initialHour = 14, initialMinute = 0
    )
    var showTime by remember { mutableStateOf(false) }


    val snackbar = remember { SnackbarHostState() }


    val scheme = MaterialTheme.colorScheme.copy(
        primary = BrandBlue,
        primaryContainer = BrandBlue,
        onPrimary = Color.White
    )

    MaterialTheme(colorScheme = scheme) {
        Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {

                Text(
                    text = "Calendario",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )


                Spacer(Modifier.height(16.dp))


                Text(
                    text = "Programa tus próximas visitas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(Modifier.height(18.dp))


                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DatePicker(
                            state = dateState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp)
                        )

                        Spacer(Modifier.height(8.dp))


                        OutlinedButton(
                            onClick = { showTime = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(String.format("%02d:%02d", timeState.hour, timeState.minute))
                        }
                    }
                }


                Spacer(Modifier.height(24.dp))

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
                ) {
                    Text("Reservar cita")
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }


    if (showTime) {
        Dialog(onDismissRequest = { showTime = false }) {
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 2.dp) {
                Column(Modifier.padding(16.dp)) {
                    TimePicker(state = timeState)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTime = false }) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { showTime = false }) { Text("OK") }
                    }
                }
            }
        }
    }
}




