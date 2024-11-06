package com.example.moodnotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moodnotes.models.MoodNote

@TypeConverters(Converters::class)
@Database(entities = [MoodNote::class], version = 1)
abstract class MoodNoteDatabase : RoomDatabase() {
    abstract fun noteDAO(): MoodNoteDao

    companion object {
        @Volatile
        private var INSTANCE: MoodNoteDatabase? = null

        fun getDatabase(context: Context): MoodNoteDatabase { // Получаем экземпляр базы данных
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodNoteDatabase::class.java,
                    "moodNote"
                ).build() // Создаем базу данных
                INSTANCE = instance
                instance
            }
        }
    }
}