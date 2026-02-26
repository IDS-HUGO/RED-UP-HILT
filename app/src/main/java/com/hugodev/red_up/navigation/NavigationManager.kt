package com.hugodev.red_up.navigation

import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

interface NavigationManager {
    fun navigate(route: String)
    fun navigateBack()
    fun navigateToPublicacionesList()
    fun navigateToCreatePublicacion()
    fun navigateToEditPublicacion(publicacionId: Int)
    fun navigateToLogin()
}


@Singleton
class NavigationManagerImpl @Inject constructor() : NavigationManager {
    
    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        navController = controller
    }

    override fun navigate(route: String) {
        navController?.navigate(route)
    }

    override fun navigateBack() {
        navController?.popBackStack()
    }

    override fun navigateToPublicacionesList() {
        navController?.navigate(Screen.HomeFeed.route) {
            popUpTo(0)
        }
    }

    override fun navigateToCreatePublicacion() {
        // Navega al home feed
        navController?.navigate(Screen.HomeFeed.route)
    }

    override fun navigateToEditPublicacion(publicacionId: Int) {
        // Navega al home feed
        navController?.navigate(Screen.HomeFeed.route)
    }

    override fun navigateToLogin() {
        navController?.navigate(Screen.Login.route) {
            popUpTo(0)
        }
    }
}
