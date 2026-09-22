package com.example.proyectofinal

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long = 1,
    val title: String,
    val content: String,
    val date: LocalDateTime,
    val category: String = "Personal",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val colorHex: String = "#151515"
) {
    @Ignore var isVisible: Boolean = true
}
