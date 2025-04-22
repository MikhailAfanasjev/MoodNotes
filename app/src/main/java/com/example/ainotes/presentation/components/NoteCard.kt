package com.example.ainotes.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ainotes.data.local.entity.Note
import com.example.linguareader.R

@Composable
fun NoteCard(note: Note, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // аналогичный ChatMessageItem
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    color = colorResource(id = R.color.ultra_light_gray),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onClick() } // обработка клика по всей карточке
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = note.note,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier
                        .size(50.dp) // квадратная форма 50x50
                        .clip(RoundedCornerShape(8.dp)) // необязательно: скругление краёв
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_remove),
                        contentDescription = "Удалить заметку",
                        modifier = Modifier.size(32.dp) // иконка внутри кнопки
                    )
                }
            }
        }
    }
}