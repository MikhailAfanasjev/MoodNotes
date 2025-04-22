package com.example.ainotes.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/**
 * Берёт Markdown-подобную строку и превращает её в AnnotatedString:
 * 1) **bold** → SpanStyle(fontWeight = Bold)
 * 2) *italic* → SpanStyle(fontStyle = Italic)
 * 3) линии, начинающиеся с "-" или "*" → "— "
 * 4) ### Заголовок → переведённый в UPPERCASE между пустыми строками
 */
fun cleanResponse(response: String): AnnotatedString {
    // 1. Сначала обрабатываем списки и заголовки в “чистый” текст:
    val preprocessed = response
        // замена "- " или "* " в начале строки на эм‑деш
        .replace(Regex("(?m)^\\s*[-*]\\s+"), "— ")
        // обработка заголовков ### — делаем UPPERCASE с заглавной буквой и изменяем шрифт
        .replace(Regex("(?m)^###\\s*(.*)$")) { m ->
            m.groupValues[1]
                .replaceFirstChar { it.uppercaseChar() }  // Переводим первую букву в верхний регистр
        }

    // 2. Регекс на inline-формат **bold** или *italic*
    val inlinePattern = Regex("\\*\\*(.*?)\\*\\*|\\*(.*?)\\*")

    // 3. Собираем AnnotatedString
    return buildAnnotatedString {
        var lastIndex = 0
        for (m in inlinePattern.findAll(preprocessed)) {
            // копируем текст между предыдущим совпадением и этим
            append(preprocessed.substring(lastIndex, m.range.first))

            // **bold**?
            m.groups[1]?.let { boldGroup ->
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(boldGroup.value)
                }
            }
            // *italic*?
            m.groups[2]?.let { italicGroup ->
                withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(italicGroup.value)
                }
            }

            lastIndex = m.range.last + 1
        }
        // остаток строки
        append(preprocessed.substring(lastIndex))
    }
}