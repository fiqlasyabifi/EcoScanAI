package com.example.ecoscanai.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garbage_history")
data class GarbageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,                 // ID otomatis bertambah (1, 2, 3...)
    val trashType: String,           // Jenis sampah (cth: Plastik)
    val confidenceScore: String,     // Skor AI (cth: 98%)
    val date: String,                // Tanggal pindaian
    val imageUri: String? = null     // Path lokasi foto di HP
)