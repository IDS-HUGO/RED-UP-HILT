package com.hugodev.red_up.features.chat.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Mail
//import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ChatsFeatureScreen(
    onOpenIndividualChats: () -> Unit,
    onOpenGroupChats: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Chats", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onOpenIndividualChats, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Email, contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Chat individual")
        }
        Button(onClick = onOpenGroupChats, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Person, contentDescription = null)
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Chat grupal")
        }
    }
}
