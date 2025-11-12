package me.santiagobrito.parkup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit


object FakeWallet {
    var balance by mutableStateOf(38_000L)
    var planExpiresAt by mutableStateOf(0L)
}


fun formatCOP(v: Long): String =
    NumberFormat.getNumberInstance(Locale("es", "CO")).format(v)


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
    navigateToMonthly: () -> Unit //
) {
    val monthlyPrice = 60_000L
    val dias = daysRemaining(FakeWallet.planExpiresAt)
    val saldo = FakeWallet.balance
    val lightBlue = Blue.copy(alpha = 0.35f) // azul claro del tema

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            PaymentTile(
                title = "Pagar mensualidad",
                subtitle = "Días restantes: $dias Día(s)",
                containerColor = lightBlue,
                onClick = navigateToMonthly
            )

            Spacer(Modifier.height(12.dp))


            PaymentTile(
                title = "Paga por horas",
                subtitle = "Saldo: ${formatCOP(saldo)}",
                containerColor = lightBlue,
                onClick = navigateToTopUp
            )

            Spacer(Modifier.height(12.dp))


            PaymentTile(
                title = "Historial de pagos",
                containerColor = lightBlue,
                onClick = navigateToHistory
            )
        }
    }
}


@Composable
private fun PaymentTile(
    title: String,
    subtitle: String? = null,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            subtitle?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}



