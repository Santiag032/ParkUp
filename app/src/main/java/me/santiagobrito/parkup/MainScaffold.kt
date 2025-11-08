package me.santiagobrito.parkup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


data class BottomItem(
    val route: String,
    val label: String,
    val iconRes: Int
)

@Composable
fun MainScaffold(mapsApiKey: String) {
    val navController = rememberNavController()

    val items = listOf(
        BottomItem("home",     "Inicio",  R.drawable.ic_home),
        BottomItem("payments", "Pagos",   R.drawable.ic_payments),
        BottomItem("profile",  "Perfil",  R.drawable.ic_profile)
    )

    Scaffold(
        containerColor = GrayM,
        bottomBar = {
            val backEntry by navController.currentBackStackEntryAsState()
            BottomBar(
                items = items,
                currentRoute = backEntry?.destination?.route,
                onSelect = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
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
            composable("home") {
                HomeScreen(
                    paddingValues = innerPadding,
                    mapsApiKey = mapsApiKey
                )
            }

            composable("payments") {
                PaymentsScreen(
                    daysLeft = 20,
                    balance = 38000,
                    onMonthly = { navController.navigate("payments/monthly") },
                    onRecharge = { navController.navigate("payments/recharge") },
                    onHistory  = { navController.navigate("payments/history") }
                )
            }
            composable("payments/monthly") {
                MonthlyPaymentScreeen(
                    currentPlan = "Mensualidad vehículo",
                    price = 120000,
                    dueInDays = 20,
                    onBack = { navController.popBackStack() },
                    onSelectMethod = { navController.navigate("payments/methods") }
                )
            }
            composable("payments/recharge") {
                RechargeBalanceScreen(
                    currentBalance = 38000,
                    onBack = { navController.popBackStack() },
                    onSelectMethod = { navController.navigate("payments/methods") }
                )
            }
            composable("payments/history") {
                PaymentHistoryScreen(onBack = { navController.popBackStack() })
            }
            composable("payments/methods") {
                PaymentMethodsSheet(onBack = { navController.popBackStack() })
            }


            composable("profile") {
                ProfileScreen(
                    name = "Usuario",
                    email = "usuario@test.com",
                    isAdmin = false,
                    daysLeft = 20,
                    appVersion = "1.0.0",
                    onEdit = { /*  */ },
                    onLogout = {
                        Firebase.auth.signOut()

                        navController.navigate("home") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
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