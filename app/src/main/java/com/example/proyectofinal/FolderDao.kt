package com.example.proyectofinal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class FolderWithCount(
    @Embedded val folder: Folder,
    val noteCount: Int
)

@Dao
interface FolderDao {
    @Query("SELECT folders.*, COUNT(notes.id) AS noteCount FROM folders LEFT JOIN notes ON folders.id = notes.folderId GROUP BY folders.id")
    fun getFoldersWithCount(): Flow<List<FolderWithCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(folder: Folder): Long

    @Update
    suspend fun update(folder: Folder)

    @Delete
    suspend fun delete(folder: Folder)

    @Query("SELECT * FROM folders WHERE id = :id")
    suspend fun getFolderById(id: Long): Folder?

    @Query("SELECT COUNT(*) FROM folders")
    suspend fun getFolderCount(): Int
}
