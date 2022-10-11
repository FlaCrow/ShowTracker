package com.flacrow.showtracker.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flacrow.showtracker.data.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class SettingsViewModel @Inject constructor(private var repository: Repository) : ViewModel() {
    protected val importDoneMutable: MutableStateFlow<Boolean> =
        MutableStateFlow(false)
    val importDone: StateFlow<Boolean> = this.importDoneMutable

    fun nukeDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.nukeDatabase()
        }
    }

    fun exportBackupFile(inputStream: FileInputStream, outputStream: OutputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.exportBackupFile(inputStream, outputStream)

        }
    }

    fun importBackupFile(inputStream: InputStream, outputStream: FileOutputStream) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.importBackupFile(inputStream, outputStream)
            importDoneMutable.update { true }
        }
    }
}