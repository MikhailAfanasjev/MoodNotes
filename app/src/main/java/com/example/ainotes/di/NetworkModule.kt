package com.example.ainotes.di

import android.content.Context
import com.example.ainotes.chatGPT.AuthInterceptor
import com.example.ainotes.chatGPT.ChatGPTApiService
import com.example.ainotes.utils.BaseUrlManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Модуль для предоставления Retrofit, API и репозитория */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideBaseUrlManager(@ApplicationContext context: Context): BaseUrlManager =
        BaseUrlManager(context)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrlManager: BaseUrlManager,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrlManager.getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideChatGPTApiService(retrofit: Retrofit): ChatGPTApiService =
        retrofit.create(ChatGPTApiService::class.java)
}