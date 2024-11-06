package com.example.moodnotes.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moodnotes.db.MoodNoteDatabase

class MoodNoteModelFactory(
 private val database: MoodNoteDatabase
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompViewModel::class.java)) {
            return CompViewModel(database) as T
        }
        throw IllegalArgumentException("Не нашел view")
    }
}