package me.santiagobrito.parkup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


data class PaymentEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val method: PayMethod,
    val amount: Long,
    val createdAt: Long = System.currentTimeMillis()
)
object FakePayments { val events = mutableStateListOf<PaymentEvent>() }


private fun cop(amount: Long): String =
    NumberFormat.getNumberInstance(Locale.US).format(amount)

private fun labelFor(type: String): String = when (type) {
    "MONTHLY" -> "Mensualidad"
    "TOPUP"   -> "Recarga saldo"
    "USAGE"   -> "Uso por horas"
    else      -> type
}

// ---------- UI ----------
@Composable
fun PaymentHistoryScreen(onBack: () -> Unit) {
    val items = FakePayments.events
    val sdf = SimpleDateFormat("dd/MM hh:mm a", Locale("es","CO"))

    Column(Modifier.fillMaxSize().padding(20.dp)) {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Spacer(Modifier.width(4.dp))
            Text("Historial de pagos", color = GrayDark)
        }

        Spacer(Modifier.height(12.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = White, tonalElevation = 1.dp) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                if (items.isEmpty()) {
                    Text("Aún no tienes pagos", color = Blue)

                } else {
                    LazyColumn {
                        items(items.sortedByDescending { it.createdAt }, key = { it.id }) { p ->
                            val date = sdf.format(Date(p.createdAt))
                                .replace(" a. m.", " a.m.")
                                .replace(" p. m.", " p.m.")
                            Text(
                                text = "${labelFor(p.type)} — ${cop(p.amount)} — $date",
                                color = Blue
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}


