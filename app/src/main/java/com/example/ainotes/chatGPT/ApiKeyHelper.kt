package com.example.ainotes.chatGPT

import android.content.Context
import android.content.SharedPreferences

object ApiKeyHelper {
    private const val PREFS_NAME = "shared_prefs"
    private const val KEY_API = "API_KEY"
    private lateinit var sharedPreferences: SharedPreferences

    // API_KEY не нужен для локально запущенных нейросетей в LM Studio,
    private const val DEFAULT_API_KEY = ""

    // Инициализация SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (getApiKey().isEmpty()) {
            storeApiKey(DEFAULT_API_KEY)
        }
    }

    // Сохраняет API‑ключ в SharedPreferences
    fun storeApiKey(apiKey: String) {
        sharedPreferences.edit().putString(KEY_API, apiKey).apply()
    }

    // Получает API‑ключ из SharedPreferences
    fun getApiKey(): String {
        return sharedPreferences.getString(KEY_API, "") ?: ""
    }
}