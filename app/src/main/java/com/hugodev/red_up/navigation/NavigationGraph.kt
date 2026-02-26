package com.hugodev.red_up.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hugodev.red_up.features.auth.presentation.screens.LoginScreen
import com.hugodev.red_up.features.auth.presentation.screens.RegisterScreen
import com.hugodev.red_up.features.auth.presentation.viewmodels.LoginViewModel
import com.hugodev.red_up.features.auth.presentation.viewmodels.RegisterViewModel
import com.hugodev.red_up.features.main.presentation.screens.MainScreen

@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController(),
    navigationManager: NavigationManager? = null,
    startDestination: String = Screen.Login.route
) {
    // Establecer el NavController en el NavigationManager singleton
    LaunchedEffect(navController) {
        if (navigationManager is NavigationManagerImpl) {
            navigationManager.setNavController(navController)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ============================================
        // AUTENTICACIÓN
        // ============================================
        
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                viewModel = viewModel
            )
        }

        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // ============================================
        // MAIN APP (Con Bottom Navigation)
        // ============================================
        
        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onNavigateToGroupDetail = { groupId ->
                    // TODO: Navegar a detalle de grupo
                },
                onNavigateToChatScreen = { userId, userName, userEmail ->

                    val encodedName = android.net.Uri.encode(userName)
                    val encodedEmail = android.net.Uri.encode(userEmail)

                    navController.navigate(
                        Screen.ChatScreen.createRoute(
                            userId,
                            encodedName,
                            encodedEmail
                        )
                    )
                }
            )
        }


        composable(
            route = Screen.ChatScreen.route
        ) { backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userName = backStackEntry.arguments?.getString("userName") ?: ""
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""

            com.hugodev.red_up.features.individual_chat.presentation.screens.IndividualChatScreen(
                userId = userId,
                userName = userName,
                userEmail = userEmail
            )
        }


    }
}
