package com.example.ainotes.utils

import kotlin.text.replace
import kotlin.text.trim

fun cleanResponse(response: String): String {
    // Преобразуем жирное выделение **текст** в обычный текст
    var cleaned = response.replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
    // Преобразуем курсивное выделение *текст* в обычный текст
    cleaned = cleaned.replace(Regex("\\*(.*?)\\*"), "$1")
    // Заменяем символы маркированного списка в начале строки на знак точки
    cleaned = cleaned.replace(Regex("(?m)^\\s*[-*]\\s+"), "— ")

    return cleaned.trim()
}