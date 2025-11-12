package me.santiagobrito.parkup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpScreen(
    onBack: () -> Unit,
    openMethods: (Long) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.filter { it.isDigit() }.toLongOrNull() ?: 0L

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recargar saldo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text("Saldo actual: ${formatCOP(FakeWallet.balance)}")
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                prefix = { Text("$ ") },
                label = { Text("Monto a recargar") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = amount > 0L,
                onClick = { openMethods(amount) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                )
            ) { Text("Elegir método de pago") }
        }
    }
}
