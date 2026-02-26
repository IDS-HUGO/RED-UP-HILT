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
import com.hugodev.red_up.features.groups.presentation.screens.GroupChatScreen
import com.hugodev.red_up.features.groups.presentation.viewmodels.GroupChatViewModel
import com.hugodev.red_up.features.home.presentation.screens.HomeScreen
import com.hugodev.red_up.features.publications.presentation.viewmodels.CreatePublicacionViewModel
import com.hugodev.red_up.features.publications.presentation.viewmodels.PublicacionesListViewModel

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
        composable(Screen.PublicacionesList.route) {
            val viewModel: PublicacionesListViewModel = hiltViewModel()
        }

        composable(Screen.CreatePublicacion.route) {
            val viewModel: CreatePublicacionViewModel = hiltViewModel()
        }

        composable(Screen.EditPublicacion.route) { backStackEntry ->
            val publicacionId = backStackEntry.arguments?.getString("publicacionId")?.toIntOrNull() ?: 0
            val viewModel: CreatePublicacionViewModel = hiltViewModel()
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
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

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToGroupChat = { groupId, groupName ->
                    navController.navigate(Screen.GroupChat.createRoute(groupId, groupName))
                }
            )
        }

        composable(Screen.GroupChat.route) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val groupName = backStackEntry.arguments?.getString("groupName") ?: ""
            val viewModel: GroupChatViewModel = hiltViewModel()
            GroupChatScreen(
                groupId = groupId,
                groupName = groupName,
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}