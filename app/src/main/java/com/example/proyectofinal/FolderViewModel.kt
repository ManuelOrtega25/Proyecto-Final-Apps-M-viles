package com.example.proyectofinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//viewmodel para controlar las operaciones y estado de las carpetas
class FolderViewModel(private val repository: FolderRepository) : ViewModel() {

    //lista en tiempo real de las carpetas con el conteo de notas
    val foldersWithCount: StateFlow<List<FolderWithCount>> = repository.foldersWithCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //funcion para crear una carpeta nueva con su icono seleccionado
    fun createFolder(name: String, iconName: String = "ic_folder_4", section: String = "En mi dispositivo") {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insert(Folder(name = name, iconName = iconName, section = section))
        }
    }

    //funcion para eliminar una carpeta
    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            repository.delete(folder)
        }
    }
}
