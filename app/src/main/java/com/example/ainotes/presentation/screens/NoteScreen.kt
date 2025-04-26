package com.example.ainotes.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ainotes.data.local.entity.Note
import com.example.ainotes.ViewModels.notes.NotesViewModel
import com.example.ainotes.presentation.components.NoteCard
import com.example.ainotes.presentation.components.NoteDetailsDialog

@Composable
fun NoteScreen(
    navController: NavHostController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val notes by viewModel.notes.collectAsState()

    // Состояние для показа диалога просмотра
    var selectedNote by remember { mutableStateOf<Note?>(null) }

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
                        onDelete = { viewModel.deleteNote(note) },
                        onEdit = {
                            navController.navigate("add_edit_note/${note.id}")
                        },
                        onClick = {
                            // при клике открываем просмотр
                            selectedNote = note
                        }
                    )
                }
            }

            // Рендерим диалог, если есть выбранная заметка
            selectedNote?.let { note ->
                NoteDetailsDialog(
                    note = note,
                    onDismiss = { selectedNote = null }
                )
            }
        }
    }
}