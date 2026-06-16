package com.example.ecoscanai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GarbageEntity::class], version = 1, exportSchema = false)
abstract class GarbageDatabase : RoomDatabase() {

    // Mengenalkan fungsi DAO ke database utama
    abstract fun garbageDao(): GarbageDao

    companion object {
        @Volatile
        private var INSTANCE: GarbageDatabase? = null

        // Fungsi Singleton: Memastikan database hanya dibuat satu kali agar tidak boros RAM HP
        fun getDatabase(context: Context): GarbageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GarbageDatabase::class.java,
                    "garbage_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}