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

    object GroupsList : Screen("groups_list")
    object CreateGroup : Screen("create_group")
    
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: Long) = "group_detail/$groupId"
    }
    
    object InviteMembers : Screen("invite_members/{groupId}") {
        fun createRoute(groupId: Long) = "invite_members/$groupId"
    }

    object Chat : Screen("chat/{roomId}/{roomName}/{roomType}") {
        fun createRoute(roomId: String, roomName: String, roomType: String) = 
            "chat/$roomId/$roomName/$roomType"
    }
}
