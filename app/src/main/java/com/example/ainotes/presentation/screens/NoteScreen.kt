package com.example.ainotes.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ainotes.mvi.notes.NotesViewModel
import com.example.ainotes.presentation.components.NoteCard
import com.example.linguareader.R

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteScreen(
    navController: NavHostController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notes) { note ->
                    NoteCard(
                        note = note,
                        onClick = {
                            // noteId = note.id — откроет экран редактирования
                            navController.navigate("add_edit_note/${note.id}")
                        },
                        onDelete = {
                            viewModel.deleteNote(note)
                        }
                    )
                }
            }

            FloatingActionButton(
                onClick = { viewModel.deleteAllNotes() },
                containerColor = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
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