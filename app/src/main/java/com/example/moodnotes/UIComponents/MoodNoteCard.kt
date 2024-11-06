package com.example.moodnotes.UIComponents

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.moodnotes.R
import com.example.moodnotes.models.MoodNote

@Composable
fun MoodNoteCard(note: MoodNote, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                Log.d("MoodNoteCard", "Clicked on note ID: ${note.id}")
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = note.mood, style = MaterialTheme.typography.titleLarge)
                Text(text = note.note, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick ={
                onDelete()
            }) {
                Image(
                    painter = painterResource(id = R.drawable.ic_remove),
                    contentDescription = "Удалить заметку",
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
        )
    }
}