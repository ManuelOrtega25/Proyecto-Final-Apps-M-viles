package com.example.act4

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val date: LocalDateTime,
    val reminderAt: LocalDateTime? = null,
    val category: String = "Personal",
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val colorHex: String = "#151515",
)
