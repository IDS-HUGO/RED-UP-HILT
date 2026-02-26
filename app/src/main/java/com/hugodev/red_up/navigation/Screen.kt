package com.hugodev.red_up.navigation

sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Principal - Con Bottom Navigation
    object Main : Screen("main")
    
    // Home Feed (Publicaciones)
    object HomeFeed : Screen("home_feed")
    object CreatePublicacion : Screen("create_publicacion")
    object EditPublicacion : Screen("edit_publicacion/{publicacionId}") {
        fun createRoute(publicacionId: Int) = "edit_publicacion/$publicacionId"
    }

    // Groups & Group Chat
    object GroupsChat : Screen("groups_chat")
    object GroupsList : Screen("groups_list")
    object CreateGroup : Screen("create_group")
    object GroupDetail : Screen("group_detail/{groupId}") {
        fun createRoute(groupId: Long) = "group_detail/$groupId"
    }
    object InviteMembers : Screen("invite_members/{groupId}") {
        fun createRoute(groupId: Long) = "invite_members/$groupId"
    }
    object GroupChatScreen : Screen("group_chat_screen/{roomId}/{roomName}") {
        fun createRoute(roomId: String, roomName: String) = "group_chat_screen/$roomId/$roomName"
    }

    // Individual Chat
    object IndividualChat : Screen("individual_chat")
    object ChatScreen : Screen("chat_screen/{userId}/{userName}/{userEmail}") {
        fun createRoute(userId: String, userName: String, userEmail: String) = 
            "chat_screen/$userId/$userName/$userEmail"
    }
    
    // Legacy (mantener para compatibilidad)
    object Chat : Screen("chat/{roomId}/{roomName}/{roomType}") {
        fun createRoute(roomId: String, roomName: String, roomType: String) = 
            "chat/$roomId/$roomName/$roomType"
    }
}
