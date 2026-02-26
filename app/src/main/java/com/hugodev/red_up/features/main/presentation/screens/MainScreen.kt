package com.hugodev.red_up.features.main.presentation.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.People
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
import com.hugodev.red_up.features.home.presentation.screens.HomeFeedScreen
import com.hugodev.red_up.features.individual_chat.presentation.screens.IndividualChatListScreen
import com.hugodev.red_up.features.groups_chat.presentation.screens.GroupsChatListScreen
import com.hugodev.red_up.features.publications.presentation.screens.CreateEditPublicacionScreen
import com.hugodev.red_up.features.groups.presentation.screens.GroupsListScreen
import com.hugodev.red_up.features.groups.presentation.screens.CreateGroupScreen
import com.hugodev.red_up.features.chat.presentation.screens.ChatScreen
import com.hugodev.red_up.navigation.Screen

enum class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    Home(Screen.HomeFeed.route, "Feed", Icons.Default.Home),
    GroupsChat(Screen.GroupsChat.route, "Grupos", Icons.Default.People),
    IndividualChat(Screen.IndividualChat.route, "Mensajes", Icons.Default.Mail)
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToGroupDetail: (String) -> Unit = {},
    onNavigateToChatScreen: (String, String, String) -> Unit = { _, _, _ -> }
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

            // Groups Chat List
            composable(Screen.GroupsChat.route) {
                selectedItem = 1
                GroupsChatListScreen(
                    onNavigateToGroupDetail = onNavigateToGroupDetail,
                    onNavigateToChatScreen = { roomId, roomName, roomType ->
                        navController.navigate(Screen.Chat.createRoute(roomId, roomName, roomType))
                    },
                    onNavigateToCreateGroup = {
                        navController.navigate(Screen.CreateGroup.route)
                    }
                )
            }

            // Individual Chat List
            composable(Screen.IndividualChat.route) {
                selectedItem = 2
                IndividualChatListScreen(
                    onNavigateToChatScreen = { userId, userName, userEmail ->
                        navController.navigate(Screen.Chat.createRoute(userId, userName, "individual"))
                    }
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
                        // Navigate to the newly created group chat
                        navController.navigate(Screen.Chat.createRoute(groupId.toString(), groupName, "grupal")) {
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
        }
    }
}
