package me.santiagobrito.parkup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max


object FakeWallet {
    var balance by mutableStateOf(38_000L)
    var planExpiresAt by mutableStateOf(0L)
}

fun formatCOP(v: Long): String =
    NumberFormat.getNumberInstance(Locale("es","CO")).format(v)

private fun daysRemaining(targetMillis: Long): Int {
    val now = System.currentTimeMillis()
    val diff = kotlin.math.max(0L, targetMillis - now)
    return (diff / TimeUnit.DAYS.toMillis(1)).toInt()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navigateToTopUp: () -> Unit,
    navigateToHistory: () -> Unit,
    openMonthlyMethods: (Long) -> Unit
) {
    val monthlyPrice = 60_000L
    val dias = daysRemaining(FakeWallet.planExpiresAt)
    val saldo = FakeWallet.balance

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Mensualidad
            Card(onClick = { openMonthlyMethods(monthlyPrice) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Pagar mensualidad", style = MaterialTheme.typography.titleMedium)
                    Text("Días restantes: $dias Día(s)")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Paga por horas → Recargar saldo
            Card(onClick = navigateToTopUp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Paga por horas", style = MaterialTheme.typography.titleMedium)
                    Text("Saldo: ${formatCOP(saldo)}")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Historial
            Card(onClick = navigateToHistory, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Historial de pagos", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

