package me.santiagobrito.parkup

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


enum class PayMethod { PSE, NEQUI, CARD, EFFECTY }

/** PANTALLA COMPLETA “Paga aquí” */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    onBack: () -> Unit,
    onSelect: (PayMethod) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paga aquí") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PayIcon(R.drawable.ic_pse, "PSE") { onSelect(PayMethod.PSE) }
                PayIcon(R.drawable.ic_nequi, "Nequi") { onSelect(PayMethod.NEQUI) }
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                PayIcon(R.drawable.ic_payments, "Tarjeta") { onSelect(PayMethod.CARD) }
                PayIcon(R.drawable.ic_efecty, "Efecty") { onSelect(PayMethod.EFFECTY) }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Selecciona un método para continuar. (Integración real se hace luego)",
                color = Color(0xFF666666),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PayIcon(
    @DrawableRes res: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Image(painter = painterResource(id = res), contentDescription = label, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(6.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurface)
    }
}


