package com.example.ainotes.chatGPT

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Content-Type", "application/json")
        // Добавляем Authorization только если ключ НЕ пустой
        ApiKeyHelper.getApiKey().takeIf { it.isNotBlank() }?.let { apiKey ->
            builder.header("Authorization", "Bearer $apiKey")
        }

        return chain.proceed(builder.build())
    }
}