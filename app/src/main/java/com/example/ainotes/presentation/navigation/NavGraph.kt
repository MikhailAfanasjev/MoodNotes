package com.example.ainotes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ainotes.mvi.notes.NotesViewModel
import com.example.ainotes.mvi.search.ChatViewModel
import com.example.ainotes.presentation.screens.AddEditNoteScreen
import com.example.ainotes.presentation.screens.ChatScreen
import com.example.ainotes.presentation.screens.NoteScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "chat",
        modifier = modifier
    ) {
        composable("chat") { backStackEntry ->
            val chatViewModel = hiltViewModel<ChatViewModel>(backStackEntry)
            ChatScreen(navController = navController, chatViewModel = chatViewModel)
        }

        // Новый маршрут без текста в URI
        composable(
            route = "add_edit_note/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val rawId = backStackEntry.arguments!!.getLong("noteId")
            val noteId: Long? = if (rawId >= 0L) rawId else null
            val viewModel = hiltViewModel<NotesViewModel>(backStackEntry)

            // Получаем текст из savedStateHandle предыдущего экрана
            val savedText = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<String>("initialText")
                .orEmpty()

            AddEditNoteScreen(
                navController = navController,
                noteId = noteId,
                viewModel = viewModel,
                initialText = savedText
            )
        }

        composable("notes") {
            NoteScreen(navController = navController)
        }
    }
}