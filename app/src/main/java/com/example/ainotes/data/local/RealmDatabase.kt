package com.example.ainotes.data.local

import android.content.Context
import io.realm.Realm
import io.realm.Realm.getDefaultModule
import io.realm.RealmConfiguration

object RealmHelper {
    fun initRealm(context: Context) {
        // 1) Инициализируем Realm
        Realm.init(context)

        // 2) Собираем конфигурацию, обязательно подключаем default‑модуль
        val config = RealmConfiguration.Builder()
            .name("notes.realm")                   // расширение .realm
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()       // на время разработки
            .modules(getDefaultModule())    // включаем все @RealmObject
            .build()

        // 3) Устанавливаем конфигурацию по‑умолчанию
        Realm.setDefaultConfiguration(config)
    }
}