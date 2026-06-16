package com.example.ecoscanai.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ecoscanai.data.local.GarbageDatabase
import com.example.ecoscanai.data.local.GarbageEntity
import com.example.ecoscanai.data.local.GarbageRepository
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GarbageRepository
    val allHistory: LiveData<List<GarbageEntity>>

    init {
        // Menghubungkan database, DAO, dan Repository saat ViewModel pertama kali dibuat
        val garbageDao = GarbageDatabase.getDatabase(application).garbageDao()
        repository = GarbageRepository(garbageDao)
        allHistory = repository.allHistory
    }

    // Fungsi untuk memasukkan data yang aman dipanggil dari Fragment/Activity lain
    fun insert(garbage: GarbageEntity) = viewModelScope.launch {
        repository.insert(garbage)
    }
}