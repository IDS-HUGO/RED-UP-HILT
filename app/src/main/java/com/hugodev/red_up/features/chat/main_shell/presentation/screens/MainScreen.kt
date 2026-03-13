package com.hugodev.red_up.features.main.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.hugodev.red_up.features.chat.presentation.screens.ChatsFeatureScreen
import com.hugodev.red_up.features.individual_chat.presentation.screens.IndividualChatListScreen
import com.hugodev.red_up.features.groups_chat.presentation.screens.GroupsChatListScreen
import com.hugodev.red_up.features.home.presentation.screens.HomeFeedScreen
import com.hugodev.red_up.features.publicaciones.comments.presentation.screens.CommentsScreen
import com.hugodev.red_up.features.publicaciones.comments.presentation.viewmodels.CommentsViewModel
import com.hugodev.red_up.features.profile.presentation.screens.EditProfileScreen
import com.hugodev.red_up.features.profile.presentation.screens.MyProfileScreen
import com.hugodev.red_up.features.profile.presentation.screens.UserProfileScreen
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileViewModel
import com.hugodev.red_up.features.publications.presentation.screens.CreateEditPublicacionScreen
import com.hugodev.red_up.features.groups.presentation.screens.CreateGroupScreen
import com.hugodev.red_up.features.groups.presentation.screens.GroupDetailScreen
import com.hugodev.red_up.features.groups.presentation.screens.InviteMembersScreen
import com.hugodev.red_up.features.chat.presentation.screens.ChatScreen
import com.hugodev.red_up.features.qr.QrScannerScreen
import com.hugodev.red_up.navigation.Screen

enum class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    Publicaciones(Screen.HomeFeed.route, "Publicaciones", Icons.Default.Article),
    Chats(Screen.ChatsHub.route, "Chats", Icons.Default.Forum),
    Perfil(Screen.Profile.route, "Perfil", Icons.Default.Person),
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedItem by remember { mutableStateOf(0) }
    val navItems = BottomNavItem.values()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.HomeFeed.route,
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            composable(Screen.HomeFeed.route) {
                selectedItem = 0
                HomeFeedScreen(
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToCreatePublication = { navController.navigate(Screen.CreatePublicacion.route) },
                    onNavigateToComments = { id -> navController.navigate(Screen.Comments.createRoute(id)) },
                    onNavigateToQrScanner = { navController.navigate(Screen.QrScanner.route) }
                )
            }

            composable(Screen.ChatsHub.route) {
                selectedItem = 1
                ChatsFeatureScreen(
                    onOpenIndividualChats = { navController.navigate(Screen.IndividualChat.route) },
                    onOpenGroupChats = { navController.navigate(Screen.GroupsChat.route) }
                )
            }

            // Groups Chat List
            composable(Screen.GroupsChat.route) {
                selectedItem = 1
                GroupsChatListScreen(
                    onNavigateToGroupDetail = { id -> navController.navigate(Screen.GroupDetail.createRoute(id.toLong())) },
                    onNavigateToChatScreen = { id, name, type -> navController.navigate(Screen.Chat.createRoute(id, name, type)) },
                    onNavigateToCreateGroup = { navController.navigate(Screen.CreateGroup.route) },
                    showCreateGroupButton = true
                )
            }

            // Individual Chat List
            composable(Screen.IndividualChat.route) {
                selectedItem = 1
                IndividualChatListScreen(
                    onNavigateToChatScreen = { id, name, _ -> navController.navigate(Screen.Chat.createRoute(id, name, "individual")) }
                )
            }

            composable(Screen.Profile.route) {
                selectedItem = 2
                MyProfileScreen(viewModel = hiltViewModel(), onNavigateBack = {}, onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) })
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(viewModel = hiltViewModel(), onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.CreatePublicacion.route) {
                CreateEditPublicacionScreen(onBackClick = { navController.popBackStack() }, onSuccess = { navController.popBackStack() })
            }

            composable(Screen.Comments.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("publicacionId")?.toLongOrNull() ?: 0L
                CommentsScreen(publicacionId = id, viewModel = hiltViewModel(), onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Chat.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("roomId") ?: ""
                val name = backStackEntry.arguments?.getString("roomName") ?: "Chat"
                val type = backStackEntry.arguments?.getString("roomType") ?: "grupal"
                ChatScreen(roomId = id, roomName = name, roomType = type, onBackClick = { navController.popBackStack() })
            }

            // Group Detail Screen
            composable(Screen.GroupDetail.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLongOrNull() ?: 0L
                GroupDetailScreen(
                    groupId = groupId,
                    onBackClick = { navController.popBackStack() },
                    onInviteMembersClick = { id -> navController.navigate(Screen.InviteMembers.createRoute(id)) },
                    onChatClick = { id, name -> navController.navigate(Screen.Chat.createRoute(id.toString(), name, "grupal")) }
                )
            }

            // Create Group Screen
            composable(Screen.CreateGroup.route) {
                CreateGroupScreen(
                    onBackClick = { navController.popBackStack() },
                    onGroupCreated = { id, _ ->
                        navController.navigate(Screen.GroupDetail.createRoute(id)) {
                            popUpTo(Screen.GroupsChat.route)
                        }
                    }
                )
            }

            // Invite Members Screen
            composable(Screen.InviteMembers.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLongOrNull() ?: 0L
                InviteMembersScreen(groupId = groupId, onBackClick = { navController.popBackStack() })
            }

            // Perfil de Usuario (Para QR)
            composable(Screen.UserProfile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull() ?: 0L
                UserProfileScreen(userId = userId, viewModel = hiltViewModel(), onNavigateBack = { navController.popBackStack() })
            }

            // QR Scanner
            composable(Screen.QrScanner.route) {
                QrScannerScreen(
                    onBackClick = { navController.popBackStack() },
                    onResultFound = { result ->
                        when {
                            result.startsWith("PROFILE-") -> {
                                val userId = result.removePrefix("PROFILE-").toLongOrNull() ?: 0L
                                navController.navigate(Screen.UserProfile.createRoute(userId)) {
                                    popUpTo(Screen.QrScanner.route) { inclusive = true }
                                }
                            }
                            result.startsWith("GROUP-") -> {
                                val groupId = result.removePrefix("GROUP-")
                                navController.navigate(Screen.Chat.createRoute(groupId, "Grupo Escaneado", "grupal")) {
                                    popUpTo(Screen.QrScanner.route) { inclusive = true }
                                }
                            }
                            result.all { it.isDigit() } -> {
                                navController.navigate(Screen.Chat.createRoute(result, "Usuario QR", "individual")) {
                                    popUpTo(Screen.QrScanner.route) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
