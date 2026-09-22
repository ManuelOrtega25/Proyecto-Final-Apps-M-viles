package com.example.proyectofinal

import kotlinx.coroutines.flow.Flow

class FolderRepository(private val folderDao: FolderDao) {
    val foldersWithCount: Flow<List<FolderWithCount>> = folderDao.getFoldersWithCount()

    suspend fun insert(folder: Folder): Long {
        return folderDao.insert(folder)
    }

    suspend fun update(folder: Folder) {
        folderDao.update(folder)
    }

    suspend fun delete(folder: Folder) {
        folderDao.delete(folder)
    }

    suspend fun getFolderById(id: Long): Folder? {
        return folderDao.getFolderById(id)
    }

    suspend fun getFolderCount(): Int {
        return folderDao.getFolderCount()
    }
}
