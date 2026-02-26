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
import com.hugodev.red_up.features.home.presentation.screens.HomeFeedScreen
import com.hugodev.red_up.features.individual_chat.presentation.screens.IndividualChatListScreen
import com.hugodev.red_up.features.groups_chat.presentation.screens.GroupsChatListScreen
import com.hugodev.red_up.navigation.Screen

enum class BottomNavItem(val route: String, val label: String, val icon: androidx.compose.material.icons.Icons.Filled) {
    Home(Screen.HomeFeed.route, "Feed", Icons.Default.Home),
    GroupsChat(Screen.GroupsChat.route, "Grupos", Icons.Default.People),
    IndividualChat(Screen.IndividualChat.route, "Mensajes", Icons.Default.Mail)
}

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToGroupDetail: (Long) -> Unit = {},
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
                    onNavigateToGroupChat = { groupId, groupName ->
                        navController.navigate(Screen.GroupChatScreen.createRoute(groupId.toString(), groupName))
                    },
                    onNavigateToIndividualChat = { userId, userName ->
                        navController.navigate(Screen.IndividualChat.route)
                    }
                )
            }

            // Groups Chat
            composable(Screen.GroupsChat.route) {
                selectedItem = 1
                GroupsChatListScreen(
                    onNavigateToGroupDetail = onNavigateToGroupDetail,
                    onNavigateToChatScreen = onNavigateToChatScreen
                )
            }

            // Individual Chat
            composable(Screen.IndividualChat.route) {
                selectedItem = 2
                IndividualChatListScreen(
                    onNavigateToChatScreen = onNavigateToChatScreen
                )
            }

            // Group Chat Screen (nested)
            composable(Screen.GroupChatScreen.route) { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId") ?: ""
                val roomName = backStackEntry.arguments?.getString("roomName") ?: ""
                
                // Aquí iría el ChatScreen para grupos
                // Por ahora es placeholder
                Text("Chat Grupal: $roomName")
            }

            // Individual Chat Screen (nested)
            composable(Screen.ChatScreen.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                val userName = backStackEntry.arguments?.getString("userName") ?: ""
                val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
                
                // Aquí iría el ChatScreen para individuales
                // Por ahora es placeholder
                Text("Chat Individual: $userName")
            }
        }
    }
}
