package me.santiagobrito.parkup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit
import kotlin.math.max

data class BottomItem(
    val route: String,
    val label: String,
    val iconRes: Int
)

@Composable
fun MainScaffold(
    mapsApiKey: String,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val firebaseUser = Firebase.auth.currentUser
    var userName by rememberSaveable { mutableStateOf(firebaseUser?.displayName ?: "Usuario") }
    var userPhone by rememberSaveable { mutableStateOf("") }
    val userEmail = firebaseUser?.email ?: "usuario@test.com"

    val items = listOf(
        BottomItem("home", "Inicio", R.drawable.ic_home),
        BottomItem("calendar", "Calendario", R.drawable.baseline_calendar_month_24),
        BottomItem("payments", "Pagos", R.drawable.ic_payments),
        BottomItem("profile", "Perfil", R.drawable.ic_profile)
    )

    Scaffold(
        bottomBar = {
            val backEntry by navController.currentBackStackEntryAsState()
            BottomBar(
                items = items,
                currentRoute = backEntry?.destination?.route,
                onSelect = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            // HOME
            composable("home") {
                HomeScreen(
                    paddingValues = innerPadding,
                    mapsApiKey = mapsApiKey,
                    onAddParking = { navController.navigate("addParking") }
                )
            }

            // CALENDARIO
            composable("calendar") { CalendarScreen() }

            // AGREGAR PARQUEADERO
            composable("addParking") {
                AddParkingScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // --- PAGOS (pantalla principal) ---
            composable("payments") {
                PaymentScreen(
                    navigateToTopUp   = { navController.navigate("topup") },
                    navigateToHistory = { navController.navigate("payments/history") },
                    navigateToMonthly = { navController.navigate("payments/monthly") }
                )
            }

            // --- PANTALLA INTERMEDIA: Pagar mensualidad ---
            composable("payments/monthly") {
                val dueDays = if (FakeWallet.planExpiresAt == 0L) 20
                else (((FakeWallet.planExpiresAt - System.currentTimeMillis())
                        / TimeUnit.DAYS.toMillis(1)).toInt().coerceAtLeast(0))

                val price = 120_000

                MonthlyPaymentScreen(
                    currentPlan    = "Mensualidad vehículo",
                    price          = price,
                    dueInDays      = dueDays,
                    onBack         = { navController.popBackStack() },
                    onSelectMethod = {
                        // ahora sí vamos a la pantalla de métodos
                        navController.navigate("paymethods/monthly/$price")
                    }
                )
            }

            // Recargar saldo
            composable("topup") {
                TopUpScreen(
                    onBack = { navController.popBackStack() },
                    openMethods = { amount ->
                        navController.navigate("paymethods/topup/$amount")
                    }
                )
            }

            // Historial de pagos
            composable("payments/history") {
                PaymentHistoryScreen(onBack = { navController.popBackStack() })
            }

            // Paga aquí (métodos) para TOPUP
            composable(
                route = "paymethods/topup/{amount}",
                arguments = listOf(navArgument("amount") { type = NavType.LongType })
            ) { backStackEntry ->
                val amount = backStackEntry.arguments?.getLong("amount") ?: 0L
                PaymentMethodScreen(
                    onBack = { navController.popBackStack() },
                    onSelect = { method ->
                        FakeWallet.balance += amount
                        FakePayments.events.add(
                            PaymentEvent(type = "TOPUP", method = method, amount = amount)
                        )
                        navController.popBackStack() // volver a TopUp
                    }
                )
            }

            // Paga aquí (métodos) para MENSUALIDAD
            composable(
                route = "paymethods/monthly/{price}",
                arguments = listOf(navArgument("price") { type = NavType.LongType })
            ) { backStackEntry ->
                val price = backStackEntry.arguments?.getLong("price") ?: 0L
                PaymentMethodScreen(
                    onBack = { navController.popBackStack() },
                    onSelect = { method ->
                        val base = max(System.currentTimeMillis(), FakeWallet.planExpiresAt)
                        FakeWallet.planExpiresAt = base + TimeUnit.DAYS.toMillis(30)
                        FakePayments.events.add(
                            PaymentEvent(type = "MONTHLY", method = method, amount = price)
                        )
                        navController.popBackStack() // volver a Payments
                    }
                )
            }

            // PERFIL
            composable("profile") {
                ProfileScreen(
                    name = userName,
                    email = userEmail,
                    isAdmin = false,
                    daysLeft = 20,
                    appVersion = "1.0.0",
                    onEdit = { navController.navigate("editProfile") },
                    onLogout = onLogout
                )
            }

            // EDITAR PERFIL
            composable("editProfile") {
                EditProfileScreen(
                    initialName = userName,
                    initialEmail = userEmail,
                    initialPhone = userPhone,
                    onBack = { navController.popBackStack() },
                    onSave = { name, phone ->
                        userName = name
                        userPhone = phone
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomBar(
    items: List<BottomItem>,
    currentRoute: String?,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Blue)
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            IconButton(onClick = { onSelect(item.route) }) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.label,
                    modifier = Modifier
                        .size(26.dp)
                        .alpha(if (selected) 1f else 0.82f)
                )
            }
        }
    }
}

