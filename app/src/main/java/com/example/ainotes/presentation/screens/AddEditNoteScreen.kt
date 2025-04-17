package com.example.ainotes.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.ainotes.mvi.notes.NotesViewModel
import com.example.linguareader.R

@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteId: Long?,
    viewModel: NotesViewModel,
    initialText: String = ""
) {
    // Преобразуем в Long? и сразу считаем флаг редактирования
    val noteIdLong: Long? = noteId?.toLong()
    val isEditing = noteIdLong != null && noteIdLong != -1L

    var title by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf(initialText) }
    val context = LocalContext.current

    val notes by viewModel.notes.collectAsState()

    // При редактировании подгружаем данные
    LaunchedEffect(isEditing, notes) {
        if (isEditing) {
            notes.find { it.id == noteIdLong }?.let { existing ->
                title = existing.title
                noteContent = existing.note
            }
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
            // Поле заголовка
            TextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("Заголовок") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .wrapContentHeight(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Spacer(Modifier.height(16.dp))

            // Поле заметки
            TextField(
                value = noteContent,
                onValueChange = { noteContent = it },
                placeholder = { Text("Заметка") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp, max = LocalConfiguration.current.screenHeightDp.dp)
                    .wrapContentHeight(),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                )
            )

            Spacer(Modifier.height(16.dp))

            // Кнопка Создать/Обновить в AddEditNoteScreen
            Button(
                onClick = {
                    if (!isEditing) {
                        viewModel.addNote(title, noteContent)
                        Toast.makeText(context, "Заметка добавлена", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.updateNote(noteIdLong!!, title, noteContent)
                        Toast.makeText(context, "Заметка обновлена", Toast.LENGTH_SHORT).show()
                    }
                    // Очистка стека и переход на NoteScreen
                    navController.navigate("notes") {
                        // Очищаем стек от всех предыдущих экранов,
                        // включая add_edit_note, чтобы не было «залипания»
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.blue)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (!isEditing) "Создать заметку" else "Обновить заметку")
            }

            Spacer(Modifier.height(16.dp))

            // Кнопка Отменить
            Button(
                onClick = {
                    navController.navigate("notes") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Отменить")
            }
        }
    }
}