package com.example.moodnotes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.moodnotes.models.MoodNote
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodNote(note: MoodNote)

    @Update
    suspend fun updateMoodNote(note: MoodNote)

    @Delete
    suspend fun deleteMoodNote(note: MoodNote)

    @Query("SELECT * FROM moodNote ORDER BY date DESC")
    fun getAllMoodNotes(): Flow<List<MoodNote>>

    @Query("DELETE FROM moodNote")
    suspend fun deleteAllMoodNotes()
}