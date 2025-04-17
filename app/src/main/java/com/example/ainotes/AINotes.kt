package com.example.ainotes

import android.app.Application
import com.example.ainotes.data.local.RealmHelper
import com.example.ainotes.utils.BaseUrlManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AINotes : Application() {
    override fun onCreate() {
        super.onCreate()

        // важно: Realm.init() вызывается до любого getDefaultInstance()
        RealmHelper.initRealm(this)

        // остальная инициализация
        val manager = BaseUrlManager(this)
        if (manager.getBaseUrl().isBlank()) {
            manager.setBaseUrl("http://192.168.1.83:1234/")
        }
    }
}