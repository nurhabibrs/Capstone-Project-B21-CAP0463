package com.dicoding.anarki.data.source.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.anarki.data.source.local.entity.PredictEntity

@Database(
    entities = [PredictEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PredictDatabase : RoomDatabase() {
    abstract fun predictDao(): PredictDao

    companion object {
        @Volatile
        private var INSTANCE: PredictDatabase? = null

        fun getInstance(context: Context): PredictDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    PredictDatabase::class.java,
                    "anarki_database"
                ).build().apply {
                    INSTANCE = this
                }
            }
    }
}