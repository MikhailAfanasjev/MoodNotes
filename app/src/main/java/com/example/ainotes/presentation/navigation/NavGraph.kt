package com.example.ainotes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ainotes.ViewModels.notes.NotesViewModel
import com.example.ainotes.ViewModels.chat.ChatViewModel
import com.example.ainotes.presentation.screens.AddEditNoteScreen
import com.example.ainotes.presentation.screens.ChatScreen
import com.example.ainotes.presentation.screens.NoteScreen
import com.example.ainotes.utils.LocalNavigationController

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = hiltViewModel(),
    notesViewModel: NotesViewModel = hiltViewModel()       // ← принимаем VM из Activity
) {
    CompositionLocalProvider(LocalNavigationController provides navController) {
        NavHost(
            navController  = navController,
            startDestination = "chat",
            modifier       = modifier
        ) {
            composable("chat") {
                ChatScreen(
                    navController = navController,
                    chatViewModel = chatViewModel
                )
            }

            composable("notes") {
                NoteScreen(
                    navController = navController,
                    viewModel     = notesViewModel
                )
            }

            composable(
                route = "add_edit_note/{noteId}",
                arguments = listOf(navArgument("noteId") {
                    type         = NavType.LongType
                    defaultValue = -1L
                })
            ) { backStackEntry ->
                val rawId = backStackEntry.arguments!!.getLong("noteId")
                val noteId: Long? = rawId.takeIf { it >= 0L }

                val initialText = navController
                    .previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("initialText")
                    .orEmpty()

                AddEditNoteScreen(
                    navController = navController,
                    viewModel     = notesViewModel,   // ← единственный экземпляр
                    noteId        = noteId,
                    initialText   = initialText
                )
            }
        }
    }
}