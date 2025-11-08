package me.santiagobrito.parkup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun InfoPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF3B82F6).copy(alpha = 0.35f))
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text,
            color = Color(0xFF111827),
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable

fun  ProfileScreen(
    name : String,
    email : String,
    isAdmin : Boolean,
    daysLeft:Int,
    appVersion: String,
    onEdit: () -> Unit,
    onLogout: () -> Unit

){
    Column(
        Modifier
            .fillMaxSize()
            .background(GrayM)
            .padding(horizontal = 20.dp)

    ) {
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color(0xFFE5E7EB))
                .align(Alignment.CenterHorizontally)

        )
        Spacer(Modifier.height(16.dp))
        Text(name, color = GrayDark, fontWeight = FontWeight.SemiBold)
        Text(email, color = GrayText)

        Spacer(Modifier.height(18.dp))
        if (isAdmin) {
            InfoPill("Eres Admin de parquearse cabecera")
        } else {
            InfoPill("Tiempo de mensualidad restante: $daysLeft Día(s)")
        }

        Spacer(Modifier.height(16.dp))
        PrimaryButton("Editar perfil", onClick = onEdit)
        Spacer(Modifier.height(12.dp))
        DangerButton("Cerrar sesión", onClick = onLogout)

        Spacer(Modifier.weight(1f))
        Text("Versión de la app: $appVersion", color = GrayText.copy(alpha = 0.6f))
        Spacer(Modifier.height(16.dp))
    }

    }


