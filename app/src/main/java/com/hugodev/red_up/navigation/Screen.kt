package com.hugodev.red_up.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object PublicacionesList : Screen("publicaciones_list")
    object CreatePublicacion : Screen("create_publicacion")

    object EditPublicacion : Screen("edit_publicacion/{publicacionId}") {
        fun createRoute(publicacionId: Int) = "edit_publicacion/$publicacionId"
    }

    object GroupChat : Screen("group_chat/{groupId}/{groupName}") {
        fun createRoute(groupId: String, groupName: String) = "group_chat/$groupId/$groupName"
    }
}
