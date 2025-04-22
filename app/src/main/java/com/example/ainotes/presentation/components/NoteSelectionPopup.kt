package com.example.ainotes.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.linguareader.R
import com.example.ainotes.utils.LocalNavigationController

@Composable
fun NoteSelectionPopup(
    selectionRect: Rect,
    density: Density,
    clipboardManager: ClipboardManager,
    onHide: () -> Unit
) {
    val context = LocalContext.current
    val navController = LocalNavigationController.current

    val offset = with(density) {
        IntOffset(
            x = selectionRect.left.toDp().roundToPx(),
            y = (selectionRect.top.toDp() - 40.dp).roundToPx()
        )
    }

    Popup(
        offset = IntOffset(offset.x, offset.y),
        properties = PopupProperties(focusable = true)
    ) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.light_gray)
            )
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                // Кнопка "Создать заметку"
                TextButton(onClick = {
                    clipboardManager.getText()?.let { annotated ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("initialText", annotated.text)

                        navController.navigate("add_edit_note/-1")
                    }
                    onHide()
                }) {
                    Text("Создать заметку", fontSize = 14.sp, color = Color.Black)
                }

                // Кнопка "Копировать"
                TextButton(onClick = {
                    clipboardManager.getText()?.let { annotated ->
                        clipboardManager.setText(AnnotatedString(annotated.text))
                    }
                    onHide()
                }) {
                    Text("Копировать", fontSize = 14.sp, color = Color.Black)
                }
            }
        }
    }
}