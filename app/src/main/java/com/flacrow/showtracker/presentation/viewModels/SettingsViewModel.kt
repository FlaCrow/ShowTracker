package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private var repository: Repository) : ViewModel() {

    fun nukeDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.nukeDatabase()
        }
    }

    fun exportFromDatabase(inputStream: FileInputStream, outputStream: OutputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.exportBackupFile(inputStream, outputStream)

        }
    }

    fun importToDatabase(inputStream: InputStream, outputStream: FileOutputStream) {
            repository.importBackupFile(inputStream, outputStream)
    }
}