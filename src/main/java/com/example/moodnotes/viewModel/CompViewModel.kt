package com.example.moodnotes.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodnotes.db.MoodNoteDatabase
import com.example.moodnotes.models.MoodNote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CompViewModel(private val database: MoodNoteDatabase) : ViewModel() {
    private val _moodNotes = MutableStateFlow<List<MoodNote>>(emptyList())
    val moodNotes = _moodNotes.asStateFlow()

    private var nextId = 3 // ID начинается с 3, так как есть две начальные заметки

    init {
        loadInitialData()
        getMoodNotes()
    }

    private fun loadInitialData() {
        val initialNotes = listOf(
            MoodNote(1, "Счастлив", "Отличный день!", LocalDateTime.of(2023, 12, 25, 10, 30)),
            MoodNote(2, "Грустный", "Плохие новости.", LocalDateTime.of(2023, 12, 24, 16, 15))
        )
        _moodNotes.value = initialNotes
    }

    private fun getMoodNotes(){
        viewModelScope.launch {
            database.noteDAO().getAllMoodNotes().collect {moodNotes ->
                _moodNotes.value = moodNotes.sortedByDescending {it.date}
            }
        }
    }

    fun addNote(mood: String, note: String) {
        val newNote = MoodNote(
            id = nextId++,
            mood = mood,
            note = note,
            date = LocalDateTime.now()
        )
        viewModelScope.launch {
            database.noteDAO().insertMoodNote(newNote)
        }
        Log.d("CompViewModel", "Added new note with id: ${newNote.id}, mood: $mood")
        getMoodNotes()
    }

    fun updateNotes(id: Int, mood: String, note: String) {
        val updatedNote = MoodNote(
            id = id,
            mood = mood,
            note = note,
            date = LocalDateTime.now()
        )
        viewModelScope.launch {
            database.noteDAO().updateMoodNote(updatedNote)
        }
        Log.d("CompViewModel", "Updated note with id: $id, new mood: $mood")
            getMoodNotes()
        }
    fun deleteMoodNote (note: MoodNote){
        viewModelScope.launch {
            database.noteDAO().deleteMoodNote(note)
            getMoodNotes()
        }
    }
    fun deleteAllMoodNotes (){
        viewModelScope.launch {
            database.noteDAO().deleteAllMoodNotes()
            getMoodNotes()
        }
    }
}