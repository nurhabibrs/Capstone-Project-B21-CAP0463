package com.dicoding.anarki.di

import android.content.Context
import com.dicoding.anarki.data.PredictRepository
import com.dicoding.anarki.data.source.local.LocalDataSource
import com.dicoding.anarki.data.source.local.room.PredictDatabase
import com.dicoding.anarki.data.source.remote.RemoteDataSource
import com.dicoding.anarki.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): PredictRepository {
        val database = PredictDatabase.getInstance(context)
        val remoteDataSource = RemoteDataSource.getInstance()
        val localDataSource = LocalDataSource.getInstance(database.predictDao())
        val appExecutors = AppExecutors()
        return PredictRepository.getInstance(remoteDataSource, localDataSource, appExecutors)
    }
}