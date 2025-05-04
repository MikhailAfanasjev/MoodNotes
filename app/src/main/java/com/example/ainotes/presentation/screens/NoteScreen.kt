package com.example.ainotes.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ainotes.data.local.entity.Note
import com.example.ainotes.viewModels.NotesViewModel
import com.example.ainotes.presentation.components.NoteCard
import com.example.ainotes.presentation.components.NoteDetailsDialog

@Composable
fun NoteScreen(
    navController: NavHostController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val colors = MaterialTheme.colorScheme
    val notes by viewModel.notes.collectAsState()
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        containerColor = colors.background
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // применяем только боковые и нижний отступ из inner, убираем верхний
                .padding(
                    start = inner.calculateStartPadding(LocalLayoutDirection.current),
                    end   = inner.calculateEndPadding(LocalLayoutDirection.current),
                    bottom= inner.calculateBottomPadding()
                )
                .background(colors.background)
        ) {
            LazyColumn(
                // добавляем боковые и вертикальные отступы по 8.dp
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.background)
            ) {
                items(notes) { note ->
                    NoteCard(
                        note     = note,
                        onDelete = { viewModel.deleteNote(note) },
                        onEdit   = { navController.navigate("add_edit_note/${note.id}") },
                        onClick  = { selectedNote = note }
                    )
                }
            }
            selectedNote?.let {
                NoteDetailsDialog(
                    note      = it,
                    onDismiss = { selectedNote = null }
                )
            }
        }
    }
}
