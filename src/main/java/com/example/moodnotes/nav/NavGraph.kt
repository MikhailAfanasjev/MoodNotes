package com.example.moodnotes.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moodnotes.screen.AddEditNoteScreen
import com.example.moodnotes.screen.HistoryScreen
import com.example.moodnotes.screen.HomeScreen
import com.example.moodnotes.viewModel.CompViewModel

@Composable
fun NavGraph(navController: NavHostController, viewModel: CompViewModel) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            Log.d("NavGraph", "Navigating to HomeScreen")
            HomeScreen(navController, viewModel)
        }
        composable("add") {
            Log.d("NavGraph", "Navigating to AddEditNoteScreen (Add mode)")
            AddEditNoteScreen(navController, noteId = null, viewModel)
        }
        composable(
            route = "edit/{noteId}",
            arguments = listOf(navArgument("noteId") { defaultValue = -1 })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getInt("noteId")
            Log.d("NavGraph", "Navigating to AddEditNoteScreen (Edit mode) for noteId: $noteId")
            AddEditNoteScreen(navController, noteId, viewModel)
        }
        composable("history") {
            Log.d("NavGraph", "Navigating to HistoryScreen")
            HistoryScreen(viewModel)
        }
    }
}