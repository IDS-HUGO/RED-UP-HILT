package com.hugodev.red_up.navigation

sealed class Screen(val route: String) {
    // Autenticación
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Principal - Con Bottom Navigation
    object Main : Screen("main")
    
    // Home Feed (Publicaciones)
    object HomeFeed : Screen("home_feed")
    object ChatsHub : Screen("chats_hub")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object CreatePublicacion : Screen("create_publicacion")
    object Comments : Screen("comments/{publicacionId}") {
        fun createRoute(publicacionId: Long) = "comments/$publicacionId"
    }
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

    // Individual Chat
    object IndividualChat : Screen("individual_chat")

    // Chat unificado (individual/grupal)
    object Chat : Screen("chat/{roomId}/{roomName}/{roomType}") {
        fun createRoute(roomId: String, roomName: String, roomType: String) = 
            "chat/$roomId/$roomName/$roomType"
    }

    // QR Scanner
    object QrScanner : Screen("qr_scanner")

    // Sync status and diagnostics
    object SyncStatus : Screen("sync_status")

    // Perfil de otro usuario
    object UserProfile : Screen("user_profile/{userId}") {
        fun createRoute(userId: Long) = "user_profile/$userId"
    }
}
