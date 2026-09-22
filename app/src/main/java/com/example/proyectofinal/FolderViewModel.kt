package com.example.proyectofinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderViewModel(private val repository: FolderRepository) : ViewModel() {

    val foldersWithCount: StateFlow<List<FolderWithCount>> = repository.foldersWithCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createFolder(name: String, iconName: String = "ic_folder_4", section: String = "En mi dispositivo") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insert(Folder(name = name, iconName = iconName, section = section))
        }
    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.delete(folder)
        }
    }
}
