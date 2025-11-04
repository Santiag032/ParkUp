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
            var navController = rememberNavController()
            var startDestination = "login"

            val auth = Firebase.auth
            val currentUser = auth.currentUser
            val mapsKey = getString(R.string.google_maps_key)

            if (currentUser != null){
                startDestination = "home"
            }else{
                startDestination = "login"
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = "login") {
                    LoginScreen(onClickRegister = {
                        navController.navigate("register")
                    }, onSuccessfulLogin = {
                        navController.navigate("home"){
                            popUpTo("login"){inclusive = true}
                        }
                    })
                }
                composable(route = "register") {
                    RegisterScreen(onClickBack = {
                        navController.popBackStack()
                    }, onSuccessfulRegister = {
                        navController.navigate("home"){
                            popUpTo(0)
                        }
                    })
                }
                composable(route = "home") {

                    HomeScreen(
                        paddingValues = PaddingValues(0.dp),
                        mapsApiKey = mapsKey
                    )
            }
        }
    }
}
}

