package com.example.act4

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: LocalDateTime,
    val category: String = "Personal",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val colorHex: String = "#151515",
    @Ignore val isVisible: Boolean = true
) {
    constructor(
        id: Long,
        title: String,
        content: String,
        date: LocalDateTime,
        category: String,
        isPinned: Boolean,
        isArchived: Boolean,
        colorHex: String
    ) : this(id, title, content, date, category, isPinned, isArchived, colorHex, true)
}
