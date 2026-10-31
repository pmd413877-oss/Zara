package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.VoiceAssistantState
import com.example.navigation.Screen
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary

@Composable
fun ZaraBottomBar(
    currentRoute: String,
    assistantState: VoiceAssistantState,
    onNavigate: (String) -> Unit,
    onOrbClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main bar container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(
                    color = ZaraSurface.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home
                BottomNavItem(
                    label = "Home",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    isSelected = currentRoute == Screen.Home.route,
                    testTag = "nav_home",
                    onClick = { onNavigate(Screen.Home.route) }
                )

                // Chat
                BottomNavItem(
                    label = "Chat",
                    icon = Icons.Outlined.Chat,
                    selectedIcon = Icons.Filled.Chat,
                    isSelected = currentRoute == Screen.Chat.route,
                    testTag = "nav_chat",
                    onClick = { onNavigate(Screen.Chat.route) }
                )

                // Space for center floating Orb
                Spacer(modifier = Modifier.size(56.dp))

                // History
                BottomNavItem(
                    label = "History",
                    icon = Icons.Outlined.History,
                    selectedIcon = Icons.Filled.History,
                    isSelected = currentRoute == Screen.ConversationHistory.route,
                    testTag = "nav_history",
                    onClick = { onNavigate(Screen.ConversationHistory.route) }
                )

                // Settings
                BottomNavItem(
                    label = "Settings",
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    isSelected = currentRoute == Screen.Settings.route,
                    testTag = "nav_settings",
                    onClick = { onNavigate(Screen.Settings.route) }
                )
            }
        }

        // Elevated Center Floating Glowing Orb
        Box(
            modifier = Modifier
                .offset(y = (-18).dp)
                .size(62.dp)
                .shadow(elevation = 12.dp, shape = CircleShape, spotColor = ZaraPurple)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ZaraPurple, ZaraCyan, Color(0xFF0F0B24))
                    )
                )
                .clickable(onClick = onOrbClick)
                .testTag("nav_orb_button"),
            contentAlignment = Alignment.Center
        ) {
            ZaraOrb(
                state = assistantState,
                size = 54.dp,
                onClick = onOrbClick
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    selectedIcon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else icon,
            contentDescription = label,
            tint = if (isSelected) ZaraPurple else ZaraTextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) ZaraTextPrimary else ZaraTextMuted
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(width = 16.dp, height = 2.dp)
                    .background(ZaraPurple, shape = RoundedCornerShape(1.dp))
            )
        }
    }
}
