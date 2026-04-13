package com.hugodev.red_up.core.ui.components.reusables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class AppCardType {
    Default,
    Success,
    Warning,
    Error
}

@Composable
fun AppCard(
    title: String,
    message: String,
    type: AppCardType = AppCardType.Default,
    modifier: Modifier = Modifier
) {
    val containerColor = when (type) {
        AppCardType.Default -> MaterialTheme.colorScheme.surface
        AppCardType.Success -> MaterialTheme.colorScheme.primaryContainer
        AppCardType.Warning -> MaterialTheme.colorScheme.tertiaryContainer
        AppCardType.Error -> MaterialTheme.colorScheme.errorContainer
    }

    val contentColor = when (type) {
        AppCardType.Default -> MaterialTheme.colorScheme.onSurface
        AppCardType.Success -> MaterialTheme.colorScheme.onPrimaryContainer
        AppCardType.Warning -> MaterialTheme.colorScheme.onTertiaryContainer
        AppCardType.Error -> MaterialTheme.colorScheme.onErrorContainer
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}