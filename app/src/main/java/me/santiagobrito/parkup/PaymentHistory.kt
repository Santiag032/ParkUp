package me.santiagobrito.parkup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable
fun PaymentHistoryScreen(onBack: () -> Unit) {
    val items = listOf(
        "Mensualidad — ${"%,d".format(120000)} — 02/11 09:15 a.m.",
        "Recarga saldo — ${"%,d".format(20000)} — 28/10 06:22 p.m.",
        "Uso por horas — ${"%,d".format(3800)} — 27/10 04:05 p.m."
    )

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Historial de pagos", fontWeight = FontWeight.Bold, color = GrayDark)
        }
        Spacer(Modifier.height(12.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = White, tonalElevation = 1.dp) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                items.forEach {
                    Text(it, color = GrayDark)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
