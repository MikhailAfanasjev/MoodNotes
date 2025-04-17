package com.example.ainotes.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavHostController
import com.example.linguareader.R

@Composable
fun NoteSelectionPopup(
    selectionRect: Rect,
    density: Density,
    clipboardManager: ClipboardManager,
    onCreateNote: (String) -> Unit,
    onHide: () -> Unit
) {
    val context = LocalContext.current

    // рассчитываем смещение в пикселях
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
                TextButton(onClick = {
                    clipboardManager.getText()?.let { annotated ->
                        // Сохраняем текст в буфер
                        clipboardManager.setText(annotated)
                        Toast.makeText(context, "Скопировано", Toast.LENGTH_SHORT).show()
                    }
                    onHide()
                }) {
                    Text("Копировать", fontSize = 14.sp, color = Color.Black)
                }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = {
                    clipboardManager.getText()?.let { annotated ->
                        onCreateNote(annotated.text)
                    }
                    onHide()
                }) {
                    Text("Создать заметку", fontSize = 14.sp, color = Color.Black)
                }
            }
        }
    }
}