package auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.etrip1.pass.BuyPassScreen
import com.example.etrip1.triptransfer.TransferPassScreen
import com.example.etrip1.ui.screens.RegisterScreen
import com.example.etrip1.qrcode.QrCodeScreen
import viewmodel.UserViewModel

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
    object QrCode : HomeTab("qrcode", "QR Code") // ✅ Added QR
}

@Composable
fun eTripNavHost(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (AuthManager.isUserLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        // LOGIN SCREEN
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    userViewModel.loadUserData()
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // REGISTER SCREEN
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    userViewModel.loadUserData()
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // FORGOT PASSWORD
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        // HOME (AFTER LOGIN)
        composable(Screen.Home.route) {
            HomeScreen(
                userViewModel = userViewModel,
                onLogout = {
                    AuthManager.logout(userViewModel) {
                        navController.navigate(Screen.Login.route) { popUpTo(0) }
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(userViewModel: UserViewModel, onLogout: () -> Unit) {
    val homeNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(homeNavController) }
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = HomeTab.HomeMain.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HomeTab.HomeMain.route) {
                HomeMainContent(userViewModel, onLogout, homeNavController) // ✅ pass navController
            }

            composable(HomeTab.BuyPass.route) {
                BuyPassScreen(navController = homeNavController)
            }
            composable(HomeTab.Transfer.route) {
                TransferPassScreen() // ViewModel is injected with viewModel()
            }

            composable(HomeTab.History.route) { HistoryScreen() }
            composable(HomeTab.Schedule.route) { ScheduleScreen() }
            composable(HomeTab.QrCode.route) { QrCodeScreen() } // ✅ QR Screen
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
        HomeTab.Schedule,
        HomeTab.QrCode // ✅ Added QR tab
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
fun HomeMainContent(userViewModel: UserViewModel, onLogout: () -> Unit, navController: NavHostController) {
    val userState by userViewModel.user.collectAsState()
    val tripsRemaining by userViewModel.tripsRemaining.collectAsState()
    val activePassType by userViewModel.activePassType.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        if (userState != null) {
            Text(
                text = "Welcome, ${userState!!.name}",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (tripsRemaining != null && activePassType != null) {
                Text(text = "${activePassType!!.replaceFirstChar { it.uppercase() }} Pass")
                Text(text = "Trips left: $tripsRemaining")
            } else {
                Text(text = "No active pass")
            }
        } else {
            Text(text = "Loading user...")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onLogout) { Text("Logout") }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Quick button to QR screen
        Button(onClick = { navController.navigate(HomeTab.QrCode.route) }) {
            Text("Show QR Code")
        }
    }
}

// Placeholder screens so build doesn’t fail
@Composable fun HistoryScreen() { Text("Trip History Screen") }
@Composable fun ScheduleScreen() { Text("Bus Schedule Screen") }
