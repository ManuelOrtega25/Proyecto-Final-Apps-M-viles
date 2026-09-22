package com.example.proyectofinal

import androidx.room.Entity
import androidx.room.PrimaryKey

//entidad de base de datos para las carpetas
@Entity(tableName = "folders")
data class Folder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iconName: String = "ic_folder_4",
    val section: String = "En mi dispositivo"
)
