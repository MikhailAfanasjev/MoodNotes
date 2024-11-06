package com.example.moodnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.moodnotes.db.MoodNoteDatabase
import com.example.moodnotes.nav.NavGraph
import com.example.moodnotes.ui.theme.MoodNotesTheme
import com.example.moodnotes.viewModel.CompViewModel
import com.example.moodnotes.viewModel.MoodNoteModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: CompViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MoodNoteDatabase.getDatabase(application)

        val viewModelFactory = MoodNoteModelFactory(database)
        viewModel = ViewModelProvider(this, viewModelFactory)[CompViewModel::class.java]

        setContent{
            MoodNotesTheme{
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}