package com.example.ecoscanai.data.local

import androidx.lifecycle.LiveData

class GarbageRepository(private val garbageDao: GarbageDao) {

    // Mengambil aliran data riwayat secara real-time
    val allHistory: LiveData<List<GarbageEntity>> = garbageDao.getAllHistory()

    // Fungsi untuk menyimpan data baru (berjalan di latar belakang agar HP tidak lag)
    suspend fun insert(garbage: GarbageEntity) {
        garbageDao.insertHistory(garbage)
    }
}