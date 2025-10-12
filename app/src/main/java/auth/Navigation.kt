package auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.etrip1.R
import com.example.etrip1.auth.AuthManager
import com.example.etrip1.auth.LoginScreen
import com.example.etrip1.history.UserHistoryScreen
import com.example.etrip1.pass.BuyPassScreen
import com.example.etrip1.qrcode.QrCodeScreen
import com.example.etrip1.schedules.ScheduleScreen
import com.example.etrip1.triptransfer.TransferPassScreen
import com.example.etrip1.ui.screens.RegisterScreen
import viewmodel.UserViewModel

// ------------------ NavHost and Routes ------------------
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
    object History : HomeTab("history", "history")
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

        // ✅ Use real Forgot Password screen
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
        bottomBar = { BottomNavigationBar(homeNavController) },
        containerColor = Color(0xFFF2F2F7)
    ) { innerPadding ->
        NavHost(
            navController = homeNavController,
            startDestination = HomeTab.HomeMain.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HomeTab.HomeMain.route) { HomeMainContent(userViewModel, onLogout, homeNavController) }
            composable(HomeTab.BuyPass.route) { BuyPassScreen(navController = homeNavController) }
            composable(HomeTab.Transfer.route) { TransferPassScreen() }
            composable(HomeTab.History.route) { UserHistoryScreen() }

            // ScheduleScreen with userName
            composable(HomeTab.Schedule.route) {
                val userState by userViewModel.user.collectAsState()
                val userName = userState?.name ?: "Traveler"
                ScheduleScreen(userName = userName)
            }

            composable(HomeTab.QrCode.route) { QrCodeScreen() }
        }
    }
}

// ------------------ Bottom Navigation ------------------
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(HomeTab.HomeMain, HomeTab.BuyPass, HomeTab.Transfer, HomeTab.History, HomeTab.Schedule)
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.4f))
            )
            NavigationBar(containerColor = Color.White) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                items.forEach { tab ->
                    val icon = when (tab) {
                        HomeTab.HomeMain -> Icons.Filled.Home
                        HomeTab.BuyPass -> Icons.Filled.ShoppingCart
                        HomeTab.Transfer -> Icons.Filled.Share
                        HomeTab.History -> Icons.Filled.History
                        HomeTab.Schedule -> Icons.Filled.Schedule
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
                        label = { Text(tab.title, color = if (isSelected) Color(0xFFFF9800) else Color.Gray) },
                        icon = { Icon(icon, contentDescription = tab.title, tint = if (isSelected) Color(0xFF1565C0) else Color.Gray) }
                    )
                }
            }
        }
    }
}

// ------------------ Home Content ------------------
@Composable
fun HomeMainContent(userViewModel: UserViewModel, onLogout: () -> Unit, navController: NavHostController) {
    val userState by userViewModel.user.collectAsState()
    val tripsRemaining by userViewModel.tripsRemaining.collectAsState()
    val activePassType by userViewModel.activePassType.collectAsState()
    val passExpiry by userViewModel.passExpiry.collectAsState()
    val daysRemaining by userViewModel.daysRemaining.collectAsState()

    // --- Color Palette for Integration ---
    val primaryBlue = Color(0xFF1E88E5)      // Main Blue
    val accentOrange = Color(0xFFFF7043)    // Main Orange
    val screenBackground = Color(0xFFF2F2F7) // Original screen background
    val cardBackground = Color(0xFFF0E5F5)         // lilac purple

    // New gradient colors
    val lightBlue = Color(0xFFFF9043) // Light Blue mid-point


    // Define the Integrated Gradient Brush: Blue (from banner) -> Orange -> Light Background
    val integratedGradientBrush = Brush.verticalGradient(
        colors = listOf( // Start color, blending seamlessly with the top of the banner
            accentOrange,
            lightBlue,// Mid-color, providing warmth
            primaryBlue // End color, fading to the original background tint
        ),
        // Extend the blend significantly down the page
        endY = 2000f
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = integratedGradientBrush) // Apply gradient to the entire Box
    ) {
        // --- 1. BANNER ILLUSTRATION (as a background element) ---
        // We assume R.drawable.banner is the image containing the illustration/skyline
        // but *without* the "Buy Your Bus Pass" text.
        Image(
            painter = painterResource(id = R.drawable.newhome), // Use your banner image
            contentDescription = null, // decorative element
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp) // Height for the illustration to cover the top
                .align(Alignment.TopCenter),
            contentScale = ContentScale.Crop
        )

        // --- 2. SCROLLABLE CONTENT (Layered over the background/illustration) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- HEADER/LOGOUT/WELCOME SECTION (OVER the Banner) ---

            // Row for Logo and Logout Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp) // Padding from the top of the screen
            ) {

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = accentOrange, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Logout", fontWeight = FontWeight.SemiBold)
                }
            }

            // Welcome Text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp), // Space below logout row
            ) {
                Text(
                    text = "Welcome back, ${userState?.name ?: "Traveler"} 👋",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White // Text color for contrast over the banner
                )
            }

            // Spacer to push the "Available Passes" text below the welcome text and above the illustration bottom
            // Adjust this height to place "Available Passes:" correctly above the cards.
            Spacer(modifier = Modifier.height(60.dp))

            // New: "Available Passes:" title
            Text(
                text = "Available Passes:",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White, // White for good contrast against the banner
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Start) // Align left like the BuyPass screen
            )
            Spacer(modifier = Modifier.height(20.dp)) // Space between title and first card

            // --- MAIN PASS/PROFILE CONTENT CARD ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    // --- PASS STATUS (From your original content) ---
                    if ((daysRemaining ?: 0) > 0) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE082)), // Warning Yellow
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "⚠ Your pass expires in $daysRemaining days",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }

                    // Active Pass Display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(primaryBlue, Color(0xFF1565C0)) // Use blue gradient for pass status
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            if (!activePassType.isNullOrEmpty() && tripsRemaining != null) {
                                Text(
                                    "${activePassType!!.replaceFirstChar { it.uppercase() }} Pass",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Trips left: $tripsRemaining", color = Color.White)
                                if (!passExpiry.isNullOrEmpty()) Text("Valid until: $passExpiry", color = Color.White)
                            } else {
                                Text("No active pass", color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User Profile Snippet (Moved inside the main card)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = accentOrange, // Use orange accent color
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Name: ${userState?.name ?: "Loading..."}", fontWeight = FontWeight.Medium, color = Color.DarkGray)
                            Text("Email: ${userState?.email ?: "Loading..."}", color = Color.Black)
                        }
                    }
                }
            }
            // --- END MAIN CARD ---

            Spacer(modifier = Modifier.height(24.dp))

            // --- QUICK ACTIONS ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                QuickActionCard("Buy Pass", Icons.Default.ShoppingCart, color = Color(0xFF0D97A1)) { navController.navigate(HomeTab.BuyPass.route) }
                QuickActionCard("Support", Icons.Default.Info, color = Color.LightGray) { }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- QR CODE BUTTON ---
            Button(
                onClick = { navController.navigate(HomeTab.QrCode.route) },
                colors = ButtonDefaults.buttonColors(containerColor = accentOrange, contentColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 20.dp)
            ) { Text("Show QR Code", fontWeight = FontWeight.SemiBold) }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun RowScope.QuickActionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.weight(1f).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current
        ) { onClick?.invoke() },
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Medium)
        }
    }
}