package me.santiagobrito.parkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import me.santiagobrito.parkup.ui.theme.ParkUpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val mapsKey = getString(R.string.google_maps_key)
            val currentUser = Firebase.auth.currentUser

            val start = if (currentUser != null) "main" else "login"

            NavHost(
                navController = navController,
                startDestination = start,
                modifier = Modifier.fillMaxSize()
            ) {
                // ---------- Auth ----------
                composable("login") {
                    LoginScreen(
                        onClickRegister = { navController.navigate("register") },
                        onSuccessfulLogin = {
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                                launchSingleTop = true
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
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // ---------- App con Bottom Nav ----------
                composable("main") {
                    MainScaffold(
                        mapsApiKey = mapsKey,
                        onLogout = {
                            // 1. Cerrar sesión en Firebase
                            Firebase.auth.signOut()

                            // 2. Ir al login y sacar "main" del back stack
                            navController.navigate("login") {
                                popUpTo("main") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

            }
        }
    }
}

