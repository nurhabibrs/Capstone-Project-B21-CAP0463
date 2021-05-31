package com.dicoding.anarki.data.source.local

import androidx.lifecycle.LiveData
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.data.source.local.room.PredictDao
import java.io.File

class LocalDataSource private constructor(private val predictDao: PredictDao) {

    fun insertResult(data: PredictEntity) {
        predictDao.insertResult(data)
    }

    fun getResult(image: String): LiveData<PredictEntity> = predictDao.getResult(image)

    companion object {
        private var INSTANCE: LocalDataSource? = null

        fun getInstance(predictDao: PredictDao): LocalDataSource =
            INSTANCE ?: LocalDataSource(predictDao)
    }
}