package com.example.proyectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyectofinal.ui.theme.Act4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as NoteApplication
        val noteViewModel = NoteViewModel(app.repository)
        val folderViewModel = FolderViewModel(app.folderRepository)

        setContent {
            Act4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    var selectedFolderState by remember { mutableStateOf<Pair<Long, String>?>(null) }

                    if (selectedFolderState == null) {
                        // Pantalla inicial: Lista de carpetas estilo Apple Notes
                        FolderListScreen(
                            folderViewModel = folderViewModel,
                            onFolderSelect = { folderId, folderName ->
                                noteViewModel.setSelectedFolderId(folderId)
                                selectedFolderState = Pair(folderId, folderName)
                            }
                        )
                    } else {
                        // Pantalla de notas dentro de la carpeta seleccionada
                        val (_, folderName) = selectedFolderState!!
                        NoteListScreen(
                            viewModel = noteViewModel,
                            folderName = folderName,
                            onBackClick = { selectedFolderState = null }
                        )
                    }
                }
            }
        }
    }
}
