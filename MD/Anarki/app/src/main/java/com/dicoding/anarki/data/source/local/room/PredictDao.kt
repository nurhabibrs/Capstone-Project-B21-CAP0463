package com.dicoding.anarki.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import java.io.File

@Dao
interface PredictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PredictEntity::class)
    fun insertResult(prediction: PredictEntity)

    @Query("SELECT * FROM predict_result_table WHERE id = :id")
    fun getResult(id: String): LiveData<PredictEntity>
}