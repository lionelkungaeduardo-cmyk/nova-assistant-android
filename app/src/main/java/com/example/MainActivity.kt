package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NovaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            val navController = rememberNavController()

            // Dynamic Runtime Permissions
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { /* Handled gracefully */ }

            LaunchedEffect(Unit) {
                val permissionsToRequest = mutableListOf(Manifest.permission.RECORD_AUDIO)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                val notGranted = permissionsToRequest.filter {
                    ContextCompat.checkSelfPermission(this@MainActivity, it) != PackageManager.PERMISSION_GRANTED
                }
                if (notGranted.isNotEmpty()) {
                    permissionLauncher.launch(notGranted.toTypedArray())
                }
            }

            NovaTheme {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val currentRoute = currentDestination?.route

                val isRootScreen = currentRoute in listOf("home", "chat", "my_day", "command_center", "settings")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NovaVoidBlack,
                    bottomBar = {
                        if (isRootScreen && userProfile?.isFirstUseDone == true) {
                            Surface(
                                color = Color(0x1A02040A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF)),
                                modifier = Modifier.testTag("bottom_navigation_bar")
                            ) {
                                NavigationBar(
                                    containerColor = Color(0x0DFFFFFF), // bg-white/5
                                    tonalElevation = 0.dp,
                                    windowInsets = NavigationBarDefaults.windowInsets
                                ) {
                                    val navItems = listOf(
                                        Triple("home", "Início", Icons.Default.Home),
                                        Triple("chat", "Chat", Icons.Default.Forum),
                                        Triple("my_day", "Meu Dia", Icons.Default.CalendarMonth),
                                        Triple("command_center", "Comando", Icons.Default.Dashboard),
                                        Triple("settings", "Ajustes", Icons.Default.Settings)
                                    )

                                    navItems.forEach { (route, label, icon) ->
                                        val isSelected = currentDestination?.hierarchy?.any { it.route == route } == true
                                        NavigationBarItem(
                                            icon = { Icon(imageVector = icon, contentDescription = label) },
                                            label = {
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                                )
                                            },
                                            selected = isSelected,
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = NovaVoidBlack,
                                                selectedTextColor = NovaCyan,
                                                indicatorColor = NovaCyan,
                                                unselectedIconColor = NovaTextMuted,
                                                unselectedTextColor = NovaTextMuted
                                            ),
                                            onClick = {
                                                if (currentRoute != route) {
                                                    navController.navigate(route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    val startDestination = if (userProfile?.isFirstUseDone == false) "onboarding" else "home"

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                viewModel = viewModel,
                                onComplete = {
                                    navController.navigate("home") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable("chat") {
                            ChatScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("my_day") {
                            MyDayScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("command_center") {
                            CommandCenterScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("technova") {
                            TechNovaScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("creative_lab") {
                            CreativeLabScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("memory_notes") {
                            MemoryNotesScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable("settings") {
                            PermissionsSettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() },
                                onRestartOnboarding = {
                                    navController.navigate("onboarding")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

