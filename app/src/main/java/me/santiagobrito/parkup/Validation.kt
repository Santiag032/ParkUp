package me.santiagobrito.parkup


import android.util.Patterns
import androidx.compose.ui.graphics.Paint
import org.intellij.lang.annotations.Pattern

fun validateEmail(email: String): Pair<Boolean, String>{
    return when{
        email.isEmpty() -> Pair(false , "El correo es requerido.")
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Pair(false,"El correo es invalido.")
        !email.endsWith("@test.com") -> Pair(false,"Este email no es corporativo.")
        else -> Pair(true, "")
    }
}

fun validatePassword(password: String): Pair<Boolean, String>{
    return when{
        password.isEmpty() -> Pair(false, "La contraseña es requerida.")
        password.length <8-> Pair(false, "la contraseña  debe tener almenos 8 caracteres.")
        !password.any{it.isDigit()} -> Pair(false, "La contraseña debe tener almenos un número.")
        else -> Pair(true,"")
    }
}

fun validateName(name: String): Pair<Boolean, String>{
    return when{
        name.isEmpty() -> Pair(false, "El nombre es requerido.")
        name.length < 3 -> Pair(false, "El nombre  debe tener almenos 8 caracteres.")
        else -> Pair(true,"")
    }
}

fun validateConfirmPassword(password: String, confirmpassword : String): Pair<Boolean, String>{
    return when{
        confirmpassword.isEmpty() -> Pair(false, "La contraseña es requerida.")
        confirmpassword != password -> Pair(false,"Las contraseñas no coinciden.")
        else -> Pair(true, "")
    }
}