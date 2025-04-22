package com.example.ainotes.data.repository

import com.example.ainotes.data.local.entity.Note
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor() {
    /**
     * Получить все заметки из Realm в виде unmanaged-копии
     */
    suspend fun getAllNotes(): List<Note> = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        try {
            val results = realm.where(Note::class.java).findAll()
            realm.copyFromRealm(results)
        } finally {
            realm.close()
        }
    }

    /**
     * Добавить или обновить заметку
     */
    suspend fun addNote(note: Note) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { bgRealm ->
                bgRealm.insertOrUpdate(note)
            }
        } finally {
            realm.close()
        }
    }

    /**
     * Удалить одну заметку по её ID
     */
    suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { bgRealm ->
                bgRealm.where(Note::class.java)
                    .equalTo("id", note.id)
                    .findFirst()
                    ?.deleteFromRealm()
            }
        } finally {
            realm.close()
        }
    }

    /**
     * Удалить все заметки
     */
    suspend fun deleteAllNotes() = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction { bgRealm ->
                val all = bgRealm.where(Note::class.java).findAll()
                all.deleteAllFromRealm()
            }
        } finally {
            realm.close()
        }
    }
}