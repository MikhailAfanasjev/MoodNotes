package com.example.moodnotes.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.moodnotes.R
import com.example.moodnotes.UIComponents.MoodNoteCard
import com.example.moodnotes.ui.theme.CustomTeal700
import com.example.moodnotes.viewModel.CompViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: CompViewModel = viewModel()) {
    val moodNotes by viewModel.moodNotes.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.d("HomeScreen", "Navigating to AddEditNoteScreen to add new note")
                    navController.navigate("add")
                },
                containerColor = CustomTeal700
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Добавить заметку",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(moodNotes) { note ->
                    MoodNoteCard(
                        note = note,
                        onClick = {
                            navController.navigate("edit/${note.id}")
                        },
                        onDelete = {
                            viewModel.deleteMoodNote(note)
                        }
                    )
                }
            }

            // Кнопка удаления в левом нижнем углу
            FloatingActionButton(
                onClick = {
                    Log.d("HomeScreen", "Deleting all notes")
                    viewModel.deleteAllMoodNotes()
                },
                containerColor = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp) // Отступ от краёв экрана
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Удалить все заметки",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    }
}