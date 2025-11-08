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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MonthlyPaymentScreeen(
    currentPlan: String,
    price: Int,
    dueInDays: Int,
    onBack: () -> Unit,
    onSelectMethod: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
            Text("Pagar mensualidad", fontWeight = FontWeight.Bold, color = GrayDark)
        }
        Spacer(Modifier.height(12.dp))

        Surface(shape = RoundedCornerShape(12.dp), color = White, tonalElevation = 1.dp) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(currentPlan, fontWeight = FontWeight.SemiBold, color = GrayDark)
                Spacer(Modifier.height(8.dp))
                Text("Valor: ${"%,d".format(price)}", color = GrayText)
                Text("Vence en: $dueInDays días", color = GrayText)
            }
        }

        Spacer(Modifier.height(20.dp))
        PrimaryButton("Elegir método de pago", onClick = onSelectMethod)
    }
}