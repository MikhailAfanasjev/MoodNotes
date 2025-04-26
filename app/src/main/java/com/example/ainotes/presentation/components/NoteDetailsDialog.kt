package com.example.ainotes.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ainotes.data.local.entity.Note

@Composable
fun NoteDetailsDialog(
    note: Note,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)    // 85% ширины
                .fillMaxHeight(0.85f),  // 85% высоты
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)       // внутренний отступ для контента
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = Int.MAX_VALUE
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note.note,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = Int.MAX_VALUE
                )
            }
        }
    }
}