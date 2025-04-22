package com.example.ainotes.presentation.components

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

@Composable
fun NoteSelectionContainer(
    onCreateNote: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val defaultToolbar = LocalTextToolbar.current
    val clipboardManager = LocalClipboardManager.current
    val density = LocalDensity.current

    var selectionRect by remember { mutableStateOf<Rect?>(null) }

    val customToolbar = remember(defaultToolbar) {
        object : TextToolbar {
            override val status: TextToolbarStatus = defaultToolbar.status

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                onCopyRequested?.invoke()
                defaultToolbar.hide()
                selectionRect = rect
            }

            override fun hide() {
                defaultToolbar.hide()
                selectionRect = null
            }
        }
    }

    CompositionLocalProvider(LocalTextToolbar provides customToolbar) {
        androidx.compose.foundation.layout.Box {
            SelectionContainer { content() }

            selectionRect?.let { rect ->
                NoteSelectionPopup(
                    selectionRect = rect,
                    density = density,
                    clipboardManager = clipboardManager,
                    onHide = { customToolbar.hide() }
                )
            }
        }
    }
}