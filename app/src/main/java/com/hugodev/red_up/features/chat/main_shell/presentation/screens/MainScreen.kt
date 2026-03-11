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
import com.hugodev.red_up.features.profile.presentation.screens.EditProfileScreen
import com.hugodev.red_up.features.profile.presentation.screens.MyProfileScreen
import com.hugodev.red_up.features.profile.presentation.viewmodels.ProfileViewModel
import com.hugodev.red_up.features.publications.presentation.screens.CreateEditPublicacionScreen
import com.hugodev.red_up.features.groups.presentation.screens.CreateGroupScreen
import com.hugodev.red_up.features.groups.presentation.screens.GroupDetailScreen
import com.hugodev.red_up.features.groups.presentation.screens.InviteMembersScreen
import com.hugodev.red_up.features.chat.presentation.screens.ChatScreen
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
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Home Feed
            composable(Screen.HomeFeed.route) {
                selectedItem = 0
                HomeFeedScreen(
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToCreatePublication = {
                        navController.navigate(Screen.CreatePublicacion.route)
                    },
                    onNavigateToGroupChat = { groupId, groupName ->
                        navController.navigate(Screen.Chat.createRoute(groupId.toString(), groupName, "grupal"))
                    },
                    onNavigateToIndividualChat = { userId, userName ->
                        navController.navigate(Screen.IndividualChat.route)
                    }
                )
            }

            composable(Screen.ChatsHub.route) {
                selectedItem = 1
                ChatsFeatureScreen(
                    onOpenIndividualChats = {
                        navController.navigate(Screen.IndividualChat.route)
                    },
                    onOpenGroupChats = {
                        navController.navigate(Screen.GroupsChat.route)
                    }
                )
            }

            // Groups Chat List
            composable(Screen.GroupsChat.route) {
                selectedItem = 1
                GroupsChatListScreen(
                    onNavigateToGroupDetail = { groupId ->
                        navController.navigate(Screen.GroupDetail.createRoute(groupId.toLong()))
                    },
                    onNavigateToChatScreen = { roomId, roomName, roomType ->
                        navController.navigate(Screen.Chat.createRoute(roomId, roomName, roomType))
                    },
                    onNavigateToCreateGroup = {},
                    showCreateGroupButton = false
                )
            }

            // Individual Chat List
            composable(Screen.IndividualChat.route) {
                selectedItem = 1
                IndividualChatListScreen(
                    onNavigateToChatScreen = { userId, userName, userEmail ->
                        navController.navigate(Screen.Chat.createRoute(userId, userName, "individual"))
                    }
                )
            }

            composable(Screen.Profile.route) {
                selectedItem = 2
                val viewModel: ProfileViewModel = hiltViewModel()
                MyProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = {},
                    onNavigateToEditProfile = {
                        navController.navigate(Screen.EditProfile.route)
                    }
                )
            }

            composable(Screen.EditProfile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                EditProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Create Publication Screen
            composable(Screen.CreatePublicacion.route) {
                CreateEditPublicacionScreen(
                    onBackClick = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            // Create Group Screen
            composable(Screen.CreateGroup.route) {
                CreateGroupScreen(
                    onBackClick = { navController.popBackStack() },
                    onGroupCreated = { groupId, groupName ->
                        // Navigate to group detail to add members
                        navController.navigate(Screen.GroupDetail.createRoute(groupId)) {
                            popUpTo(Screen.GroupsChat.route)
                        }
                    }
                )
            }

            // Chat Screen (both group and individual)
            composable(Screen.Chat.route) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
                val roomType = backStackEntry.arguments?.getString("roomType") ?: "grupal"
                
                ChatScreen(
                    roomId = roomId,
                    roomName = roomName,
                    roomType = roomType,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // Group Detail Screen
            composable(Screen.GroupDetail.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLongOrNull() ?: 0L
                
                GroupDetailScreen(
                    groupId = groupId,
                    onBackClick = { navController.popBackStack() },
                    onInviteMembersClick = { gId ->
                        navController.navigate(Screen.InviteMembers.createRoute(gId))
                    },
                    onChatClick = { gId, gName ->
                        navController.navigate(Screen.Chat.createRoute(gId.toString(), gName, "grupal"))
                    }
                )
            }

            // Invite Members Screen
            composable(Screen.InviteMembers.route) { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")?.toLongOrNull() ?: 0L
                
                InviteMembersScreen(
                    groupId = groupId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
