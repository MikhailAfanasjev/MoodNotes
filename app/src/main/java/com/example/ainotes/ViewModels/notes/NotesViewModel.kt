package com.example.ainotes.ViewModels.notes

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainotes.data.local.entity.Note
import com.example.ainotes.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        loadNotes()
    }

    /**
     * Загрузить все заметки и обновить StateFlow
     */
    private fun loadNotes() {
        viewModelScope.launch {
            val list = noteRepository.getAllNotes()
            Log.d("NotesViewModel", "Loaded notes: ${'$'}{list.size}")
            _notes.value = list
        }
    }

    /**
     * Создать новую заметку
     */
    fun addNote(title: String, content: String) {
        val newNote = Note().apply {
            id = System.currentTimeMillis()
            this.title = title
            note = content
        }
        viewModelScope.launch {
            noteRepository.addNote(newNote)
            loadNotes()
        }
    }

    /**
     * Обновить существующую заметку
     */
    fun updateNote(noteId: Long, title: String, content: String) {
        viewModelScope.launch {
            val list = noteRepository.getAllNotes()
            list.find { it.id == noteId }?.let {
                it.title = title
                it.note = content
                noteRepository.addNote(it)
                loadNotes()
            }
        }
    }

    /**
     * Удалить одну заметку
     */
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
            loadNotes()
        }
    }

    /**
     * Удалить все заметки
     */
    fun deleteAllNotes() {
        viewModelScope.launch {
            noteRepository.deleteAllNotes()
            loadNotes()
        }
    }
}
