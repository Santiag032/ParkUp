package me.santiagobrito.parkup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PaymentsScreen(
    daysLeft: Int,
    balance: Int,
    onMonthly: () -> Unit,
    onRecharge: () -> Unit,
    onHistory: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Pagos", color = GrayDark, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))


        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Blue.copy(alpha = 0.35f),
            onClick = onMonthly
        ) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Pagar mensualidad", fontWeight = FontWeight.SemiBold, color = GrayDark)
                Spacer(Modifier.height(6.dp))
                Text("Días restantes: $daysLeft Día(s)", color = GrayText)
            }
        }

        Spacer(Modifier.height(18.dp))


        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Blue.copy(alpha = 0.35f),
            onClick = onRecharge
        ) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Paga por horas", fontWeight = FontWeight.SemiBold, color = GrayDark)
                Spacer(Modifier.height(6.dp))
                Text("Saldo: ${"%,d".format(balance)}", color = GrayText)
            }
        }

        Spacer(Modifier.height(18.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Blue.copy(alpha = 0.35f),
            onClick = onHistory
        ) {
            Box(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("Historial de pagos", fontWeight = FontWeight.SemiBold, color = GrayDark)
            }
        }
    }
}