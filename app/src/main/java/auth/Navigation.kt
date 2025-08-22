package auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.etrip1.pass.BuyPassScreen
import com.example.etrip1.triptransfer.PassManager
import com.example.etrip1.triptransfer.TransferPassScreen
import com.example.etrip1.ui.screens.RegisterScreen

// Routes for authentication
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
}

// Routes inside the main app (after login)
sealed class HomeTab(val route: String, val title: String) {
    object HomeMain : HomeTab("home_main", "Home")
    object BuyPass : HomeTab("buy_pass", "Buy Pass")
    object Transfer : HomeTab("transfer", "Transfer")
    object History : HomeTab("history", "History")
    object Schedule : HomeTab("schedule", "Schedule")
}

@Composable
fun eTripNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (AuthManager.isUserLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        // LOGIN SCREEN
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // REGISTER SCREEN
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // FORGOT PASSWORD SCREEN
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // HOME (MAIN APP) WITH BOTTOM NAVIGATION
        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    AuthManager.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onLogout: () -> Unit) {
    val homeNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(homeNavController) }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = HomeTab.HomeMain.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HomeTab.HomeMain.route) { HomeMainContent(onLogout) }

            composable(HomeTab.BuyPass.route) {
                val navController = null
                BuyPassScreen(navController = navController)
            }
            composable(HomeTab.Transfer.route) {
                val passManager = PassManager() // or get from a ViewModel / DI
                TransferPassScreen(passManager = passManager)
            }

            composable(HomeTab.History.route) { HistoryScreen() }
            composable(HomeTab.Schedule.route) { ScheduleScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        HomeTab.HomeMain,
        HomeTab.BuyPass,
        HomeTab.Transfer,
        HomeTab.History,
        HomeTab.Schedule
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { tab ->
            NavigationBarItem(
                selected = currentRoute == tab.route,
                onClick = {
                    navController.navigate(tab.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(tab.title) },
                icon = { Icon(Icons.Default.Home, contentDescription = tab.title) }
            )
        }
    }
}

@Composable
fun HomeMainContent(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to eTrip Home", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogout) {
            Text("Logout")
        }
    }
}

// Placeholder screens
@Composable fun BuyPassScreen() { Text("Buy Pass Screen") }
@Composable fun TransferScreen() { Text("Transfer Trips Screen") }
@Composable fun HistoryScreen() { Text("Trip History Screen") }
@Composable fun ScheduleScreen() { Text("Bus Schedule Screen") }
