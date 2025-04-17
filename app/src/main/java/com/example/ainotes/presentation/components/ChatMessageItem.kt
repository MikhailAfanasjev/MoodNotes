package com.example.ainotes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ainotes.chatGPT.Message
import com.example.linguareader.R

@Composable
fun ChatMessageItem(
    message: Message,
    onCreateNote: (String) -> Unit
) {
    val isAssistant = message.role == "assistant"
    val rowArrangement = if (isAssistant) Arrangement.Start else Arrangement.End

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = rowArrangement
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .background(
                    color = colorResource(id = R.color.ultra_light_gray),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ) {
            NoteSelectionContainer(onCreateNote = onCreateNote) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                    modifier = Modifier.widthIn(max = 280.dp),
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}