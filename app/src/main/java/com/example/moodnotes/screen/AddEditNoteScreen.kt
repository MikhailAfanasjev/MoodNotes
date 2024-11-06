package com.example.moodnotes.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moodnotes.ui.theme.CustomTeal200
import com.example.moodnotes.ui.theme.CustomTeal700
import com.example.moodnotes.viewModel.CompViewModel

@Composable
fun AddEditNoteScreen(navController: NavController, noteId: Int?, viewModel: CompViewModel) {
    var mood by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (noteId != null && noteId != -1) {
        val existingNote = viewModel.moodNotes.collectAsState().value.find { it.id == noteId }
        existingNote?.let {
            Log.d("AddEditNoteScreen", "Editing note ID: $noteId")
            mood = it.mood
            note = it.note
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = mood,
                onValueChange = {
                    mood = it
                },
                label = { Text("Настроение") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = note,
                onValueChange = {
                    note = it
                },
                label = { Text("Заметка") },
                modifier = Modifier.fillMaxHeight(0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (noteId == null) {
                        Log.d("AddEditNoteScreen", "Adding new note: mood=$mood, note=$note")
                        viewModel.addNote(mood, note)
                        Toast.makeText(context, "Заметка добавлена", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("AddEditNoteScreen", "Updating note ID: $noteId with mood=$mood, note=$note")
                        viewModel.updateNotes(noteId, mood, note)
                        Toast.makeText(context, "Заметка обновлена", Toast.LENGTH_SHORT).show()
                    }
                    navController.navigate("home")
                },
                colors = ButtonDefaults.buttonColors(containerColor = CustomTeal700) // Добавляем цвет кнопке
            ) {
                Text(if (noteId == null) "Создать заметку" else "Обновить заметку")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
               onClick = {navController.navigate("home")},
               colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Отменить")
            }
        }
    }
}