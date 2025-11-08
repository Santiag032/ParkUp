package me.santiagobrito.parkup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



val Blue = Color(0xFF3B82F6)
val Bluelight = Color(0xFFAED1FF)
val Red = Color(0xFFEF4444)
val GrayM = Color(0xFFF9FAFB)
val GrayText = Color(0xFF6B7280)
val GrayDark = Color(0xFF111827)
val White = Color(0xFFFFFFFF)
val Border = Color(0xFFE5E7EB)



@Composable

fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: ()-> Unit){

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        onClick = onClick,
        color = Blue,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            Text(text, color = White, fontWeight = FontWeight.SemiBold)
        }

    }
}

@Composable

fun DangerButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),                 // << altura fija
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Red)
    ) {
        Text(text, color = White, fontWeight = FontWeight.SemiBold)
    }
}
    //Cards
    @Composable
    fun String.InfoPill(
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Blue.copy(alpha = 0.35f))
                .padding(vertical = 16.dp, horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(this@InfoPill, color = GrayDark, fontWeight = FontWeight.Bold)
        }
    }
    //Cards Stadistics
    @Composable
    fun StatCard(
        title: String,
        value: String,
        modifier: Modifier = Modifier,
        highlight: Boolean = false
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (highlight) Blue else White)
                .borderIf(!highlight, Border)
                .padding(16.dp)
        ) {
            Column {
                Text(title, color = if (highlight) White.copy(0.9f) else GrayText)
                Spacer(Modifier.height(6.dp))
                Text(
                    value,
                    color = if (highlight) White else GrayDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

private fun Modifier.borderIf(condition: Boolean, color: Color) =
    if (condition) this.then(Modifier
        .background(White)
        .padding(0.dp)) else this