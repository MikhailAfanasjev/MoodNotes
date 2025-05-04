package com.example.ainotes.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.ainotes.chatGPT.Message
import com.example.ainotes.presentation.ui.theme.Blue
import com.example.ainotes.presentation.ui.theme.UltraLightGray
import com.example.linguareader.R

@Composable
fun ChatMessageItem(
    message: Message,
    onCreateNote: (String) -> Unit
) {
    val isAssistant = message.role == "assistant"
    val rowArrangement = if (isAssistant) Arrangement.Start else Arrangement.End

    val bubbleShape = if (isAssistant) {
        RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 16.dp,
            bottomEnd = 16.dp,
            bottomStart = 16.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 0.dp,
            bottomEnd = 16.dp,
            bottomStart = 16.dp
        )
    }

    // Используем цвета из темы
    val colorScheme = MaterialTheme.colorScheme
    val bubbleColor = if (isAssistant) {
        colorScheme.onPrimary // цвет bubble пользователя
    } else {
        colorScheme.primary // цвет bubble ИИ
    }
    val contentColor = colorScheme.onSecondary

    if (message.content.isNotBlank()) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = rowArrangement
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f),
                    contentAlignment = if (isAssistant) Alignment.CenterStart else Alignment.CenterEnd
                ) {
                    // Оборачиваем в локальный цвет контента
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        NoteSelectionContainer(
                            text = message.content,
                            onCreateNote = onCreateNote,
                            modifier = Modifier
                                .widthIn(min = 48.dp)
                                .background(color = bubbleColor, shape = bubbleShape)
                                .padding(8.dp)
                                .animateContentSize()
                        )
                    }
                }
            }
        }
    }
}