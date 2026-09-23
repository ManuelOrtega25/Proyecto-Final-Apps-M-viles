package com.example.proyectofinal

import android.app.Application
import androidx.room.Room

class NoteApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            this,
            NoteDatabase::class.java,
            "note_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val repository by lazy { NoteRepository(database.noteDao()) }
}
