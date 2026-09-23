package com.example.proyectofinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // filtros
    val allNotes: StateFlow<List<Note>> = combine(
        repository.allNotes,
        _searchQuery,
        _selectedCategory
    ) { notes, query, category ->
        notes.filter { note ->
            val matchesQuery = query.isEmpty() ||
                note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true)

            if (category == "Archivadas") {
                note.isArchived && matchesQuery
            } else {
                val notArchived = !note.isArchived
                val matchesCategory = category == "Todas" || note.category.equals(category, ignoreCase = true)
                notArchived && matchesQuery && matchesCategory
            }
        }.sortedWith(
            compareByDescending<Note> { it.isPinned }.thenByDescending { it.date }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rawNotesList: StateFlow<List<Note>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun insertNote(note: Note, onInserted: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val noteId = repository.insert(note)
            onInserted(noteId)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun archiveNote(note: Note) {
        viewModelScope.launch {
            repository.update(note.copy(isArchived = true))
        }
    }

    fun unarchiveNote(note: Note) {
        viewModelScope.launch {
            repository.update(note.copy(isArchived = false))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun togglePin(note: Note) {
        viewModelScope.launch {
            repository.update(note.copy(isPinned = !note.isPinned))
        }
    }
}
