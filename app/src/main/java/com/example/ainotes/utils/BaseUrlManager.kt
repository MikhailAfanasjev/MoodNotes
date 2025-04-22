package com.example.ainotes.utils

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class BaseUrlManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "secure_prefs"
        private const val KEY_BASE_URL = "key_base_url"
        private const val DEFAULT_URL = "https://9105-84-17-46-88.ngrok-free.app"
        private const val TAG = ">>>BaseUrlManager"
    }

    private val sharedPrefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getBaseUrl(): String {
        val baseUrl = sharedPrefs.getString(KEY_BASE_URL, DEFAULT_URL) ?: DEFAULT_URL
        Log.d(TAG, "getBaseUrl: Returning base URL: $baseUrl")
        return baseUrl
    }

    fun setBaseUrl(url: String) {
        Log.d(TAG, "setBaseUrl: Setting base URL to: $url")
        sharedPrefs.edit().putString(KEY_BASE_URL, url).apply()
    }

    fun updateBaseUrlFromNgrok() {
        Log.d(TAG, "updateBaseUrlFromNgrok: Starting Ngrok tunnels fetch")
        FetchNgrokTunnelsTask(this).execute()
    }
}

//class BaseUrlManager(context: Context) {
//    companion object {
//        private const val PREFS_NAME = "secure_prefs"
//        private const val KEY_BASE_URL = "key_base_url"
//        private const val DEFAULT_URL = "https://a398-84-17-46-88.ngrok-free.app"
//    }
//
//    private val sharedPrefs = EncryptedSharedPreferences.create(
//        context,
//        PREFS_NAME,
//        MasterKey.Builder(context)
//            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
//            .build(),
//        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//    )
//
//    fun getBaseUrl(): String =
//        sharedPrefs.getString(KEY_BASE_URL, DEFAULT_URL)!!
//
//    fun setBaseUrl(url: String) {
//        sharedPrefs.edit()
//            .putString(KEY_BASE_URL, url)
//            .apply()
//    }
//}