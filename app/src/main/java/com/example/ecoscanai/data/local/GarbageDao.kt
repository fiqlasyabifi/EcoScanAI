package com.example.ecoscanai.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GarbageDao {
    // Fungsi untuk memasukkan data riwayat baru setelah kamera sukses scan sampah
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHistory(garbage: GarbageEntity)

    // Fungsi untuk mengambil semua data riwayat, diurutkan dari yang paling baru
    @Query("SELECT * FROM garbage_history ORDER BY id DESC")
    fun getAllHistory(): LiveData<List<GarbageEntity>>
}