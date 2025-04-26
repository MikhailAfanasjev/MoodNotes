package com.example.ainotes.utils

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor(
    private val baseUrlManager: BaseUrlManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val currentBase = baseUrlManager.getBaseUrl()
        val newBaseUrl = currentBase.toHttpUrlOrNull()
            ?: return chain.proceed(req)  // на случай некорректного URL

        val newUrl = req.url
            .newBuilder()
            .scheme(newBaseUrl.scheme)
            .host(newBaseUrl.host)
            .port(newBaseUrl.port)
            .build()

        val newReq = req.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newReq)
    }
}