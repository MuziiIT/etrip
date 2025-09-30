package com.example.etrip1.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import auth.ForgotPasswordScreen
import com.example.etrip1.R
import com.example.etrip1.pass.BuyPassScreen
import com.example.etrip1.qrcode.QrCodeScreen
import com.example.etrip1.schedules.ScheduleScreen
import com.example.etrip1.triptransfer.TransferPassScreen
import com.example.etrip1.ui.screens.RegisterScreen
import viewmodel.UserViewModel

// ------------------ Navigation Routes ------------------
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
}

sealed class HomeTab(val route: String, val title: String) {
    object HomeMain : HomeTab("home_main", "Home")
    object BuyPass : HomeTab("buy_pass", "Buy Pass")
    object Transfer : HomeTab("transfer", "Transfer")
    object History : HomeTab("history", "History")
    object Schedule : HomeTab("schedule", "Schedule")
    object QrCode : HomeTab("qrcode", "QR Code")
}

// ------------------ NavHost ------------------
@Composable
fun eTripNavHost(userViewModel: UserViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (AuthManager.isUserLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
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
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    userViewModel.loadUserData()
                    navController.navigate(Screen.Home.route) { popUpTo(0) }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }
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

// ------------------ Home Screen ------------------
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
            composable(HomeTab.HomeMain.route) { HomeMainContent(userViewModel, onLogout, homeNavController) }
            composable(HomeTab.BuyPass.route) { BuyPassScreen(navController = homeNavController) }
            composable(HomeTab.Transfer.route) { TransferPassScreen() }
            composable(HomeTab.History.route) { HistoryScreen() }
            composable(HomeTab.Schedule.route) { ScheduleScreen() }   // ✅ using the real one
            composable(HomeTab.QrCode.route) { QrCodeScreen() }
        }
    }
}

// ------------------ Bottom Navigation ------------------
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(HomeTab.HomeMain, HomeTab.BuyPass, HomeTab.Transfer, HomeTab.History, HomeTab.Schedule)
    Surface(shadowElevation = 4.dp, color = Color.White) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray.copy(alpha = 0.3f))
            )
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { tab ->
                    val icon = when (tab) {
                        HomeTab.HomeMain -> Icons.Filled.Home
                        HomeTab.BuyPass -> Icons.Filled.ShoppingCart
                        HomeTab.Transfer -> Icons.Filled.Share
                        HomeTab.History -> Icons.Filled.Refresh
                        HomeTab.Schedule -> Icons.AutoMirrored.Filled.List
                        else -> Icons.Filled.Home
                    }
                    val isSelected = currentRoute == tab.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(tab.title, color = if (isSelected) Color(0xFF1565C0) else Color.Black) },
                        icon = { Icon(icon, contentDescription = tab.title, tint = if (isSelected) Color(0xFF1565C0) else Color.Black) }
                    )
                }
            }
        }
    }
}

// ------------------ Main Home Content ------------------
@Composable
fun HomeMainContent(userViewModel: UserViewModel, onLogout: () -> Unit, navController: NavHostController) {
    val userState by userViewModel.user.collectAsState()
    val tripsRemaining by userViewModel.tripsRemaining.collectAsState()
    val activePassType by userViewModel.activePassType.collectAsState()
    val passExpiry by userViewModel.passExpiry.collectAsState()
    val daysRemaining by userViewModel.daysRemaining.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        //LOGO
        Image(
            painter = painterResource(id = R.drawable.logoo),
            contentDescription = "App Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .shadow(4.dp, CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome back, ${userState?.name ?: "Traveler"} 👋",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1565C0)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Warning if pass expires
        if ((daysRemaining ?: 0) > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE082)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "⚠ Your pass expires in $daysRemaining days",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }
        // Active Pass Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (!activePassType.isNullOrEmpty() && tripsRemaining != null) {
                    Text("${activePassType!!.replaceFirstChar { it.uppercase() }} Pass", color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text("Trips left: $tripsRemaining", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                    if (!passExpiry.isNullOrEmpty()) Text("Valid until: $passExpiry", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text("No active pass", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        // Profile Card with Icon
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Icon",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Name: ${userState?.name ?: "Loading..."}", color = Color.White)
                    Text("Email: ${userState?.email ?: "Loading..."}", color = Color.White)
                }
            }
        }
        // Quick Action Cards - UPDATED WITH TOP UP FUNCTIONALITY
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            QuickActionCard(
                title = "Top Up",
                icon = Icons.Default.Add,
                color = Color(0xFF4CAF50),
                onClick = { navController.navigate(HomeTab.BuyPass.route) }
            )
            QuickActionCard(
                title = "Support",
                icon = Icons.Default.Info,
                color = Color(0xFF03A9F4)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Buttons
        Button(
            onClick = { navController.navigate(HomeTab.QrCode.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0), contentColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Show QR Code") }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800), contentColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Logout") }
    }
}

// ------------------ Extra Components ------------------
@Composable
fun RowScope.QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = { onClick?.invoke() }
            ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ------------------ Placeholders ------------------
@Composable fun HistoryScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Trip History Screen", style = MaterialTheme.typography.headlineMedium)
    }
}
