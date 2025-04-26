package com.example.ainotes.presentation.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ainotes.chatGPT.Message
import com.example.ainotes.ViewModels.chat.ChatViewModel
import com.example.ainotes.ViewModels.notes.NotesViewModel
import com.example.ainotes.presentation.ui.theme.Blue
import com.example.ainotes.presentation.ui.theme.LightGray
import com.example.ainotes.presentation.ui.theme.White
import com.example.ainotes.utils.NoRippleTheme
import com.example.linguareader.R

@Composable
fun BottomBar(
    navController: NavController,
    chatViewModel: ChatViewModel= hiltViewModel(),
    chatMessages: List<Message>,
    notesViewModel: NotesViewModel = hiltViewModel()
) {
    val iconSize = 24.dp
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry
        ?.destination
        ?.route
        ?.substringBefore("/")
        ?: ""
    val notes by notesViewModel.notes.collectAsState()
    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        NavigationBar(
            containerColor = White,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            // ——— CHAT ITEM ———
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chat),
                        contentDescription = null,
                        tint = if (currentRoute == "chat") Blue else LightGray,
                        modifier = Modifier.size(iconSize)
                    )
                },
                label = { Text(text = stringResource(R.string.chat)) },
                selected = currentRoute == "chat",
                onClick = {
                    navController.navigate("chat") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                interactionSource = interactionSource,
                colors = NavigationBarItemDefaults.colors(indicatorColor = White)
            )

            // ——— NOTES ITEM ———
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_notes),
                        contentDescription = null,
                        tint = if (currentRoute == "notes") Blue else LightGray,
                        modifier = Modifier.size(iconSize)
                    )
                },
                label = { Text(text = stringResource(R.string.notes)) },
                selected = currentRoute == "notes",
                onClick = {
                    navController.navigate("notes") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                interactionSource = interactionSource,
                colors = NavigationBarItemDefaults.colors(indicatorColor = White)
            )

            // ——— CLEAR CHAT BUTTON ———
            if (currentRoute == "chat" && chatMessages.isNotEmpty()) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = stringResource(R.string.clear_chat),
                            modifier = Modifier.size(iconSize),
                            tint = Color.Red
                        )
                    },
                    label = { Text(text = stringResource(R.string.clear_chat)) },
                    selected = false,
                    onClick = { chatViewModel.clearChat() },
                    interactionSource = interactionSource,
                    colors = NavigationBarItemDefaults.colors(indicatorColor = White)
                )
            }

            // ——— DELETE ALL NOTES BUTTON ———
            if (currentRoute == "notes" && notes.isNotEmpty()) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_remove),
                            contentDescription = stringResource(R.string.delete_all_notes),
                            modifier = Modifier.size(iconSize),
                            tint = Color.Red
                        )
                    },
                    label = { Text(text = stringResource(R.string.delete_all_notes)) },
                    selected = false,
                    onClick = { notesViewModel.deleteAllNotes() },
                    interactionSource = remember { MutableInteractionSource() },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = White)
                )
            }
        }
    }
}