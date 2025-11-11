package me.santiagobrito.parkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.santiagobrito.parkup.ui.theme.ParkUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ParkUpApp()
        }
    }
}

@Composable
fun ParkUpApp() {
    ParkUpTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

            val navController = rememberNavController()
            val currentUser = Firebase.auth.currentUser

            // 👇 TU API KEY DE MAPS (por ahora directa; luego puedes moverla a BuildConfig o strings)
            val mapsApiKey = "TU_API_KEY_DE_GOOGLE_MAPS_AQUI"

            // Si está logueado va directo al main, si no al login
            val startDestination = if (currentUser != null) "main" else "login"

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {

                // ---------- Auth ----------
                composable("login") {
                    LoginScreen(
                        onClickRegister = { navController.navigate("register") },
                        onSuccessfulLogin = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        onClickBack = { navController.popBackStack() },
                        onSuccessfulRegister = {
                            navController.navigate("main") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }

                // ---------- App principal con bottom nav ----------
                composable("main") {
                    MainScaffold(
                        mapsApiKey = mapsApiKey,
                        onLogout = {
                            Firebase.auth.signOut()
                            navController.navigate("login") {
                                popUpTo("main") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}


