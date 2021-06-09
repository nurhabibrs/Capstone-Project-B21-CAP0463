package com.dicoding.anarki.data.source.local.room

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.dicoding.anarki.data.source.local.entity.PredictEntity

@Dao
interface PredictDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PredictEntity::class)
    fun insertResult(prediction: PredictEntity)


    @Query("SELECT * FROM predict_result_table WHERE id = :id")
    fun getResult(id: String): LiveData<PredictEntity>

    @Query("SELECT * FROM predict_result_table")
    fun getHistory():  DataSource.Factory<Int, PredictEntity>

    @Query("DELETE FROM predict_result_table")
    fun deleteHistory()

}