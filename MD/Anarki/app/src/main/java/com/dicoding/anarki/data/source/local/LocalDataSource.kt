package com.dicoding.anarki.data.source.local

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.data.source.local.room.PredictDao

class LocalDataSource private constructor(private val predictDao: PredictDao) {

    fun insertResult(data: PredictEntity) {
        predictDao.insertResult(data)
    }

    fun getResult(image: String): LiveData<PredictEntity> = predictDao.getResult(image)

    fun getHistory() : DataSource.Factory<Int, PredictEntity> = predictDao.getHistory()

    fun deleteHistory(){
        predictDao.deleteHistory()
    }

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(predictDao: PredictDao): LocalDataSource =
            INSTANCE ?: LocalDataSource(predictDao)
    }
}