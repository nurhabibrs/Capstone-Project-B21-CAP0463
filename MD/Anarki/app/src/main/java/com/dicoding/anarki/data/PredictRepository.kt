package com.dicoding.anarki.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.dicoding.anarki.data.source.local.LocalDataSource
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.data.source.remote.ApiResponse
import com.dicoding.anarki.data.source.remote.RemoteDataSource
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.data.source.remote.response.PredictResponse
import com.dicoding.anarki.utils.AppExecutors
import com.dicoding.anarki.vo.Resource
import java.io.File

class PredictRepository private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : PredictDataSource {
    override fun getResult(context: Context, file: File, body: UploadRequest): LiveData<Resource<PredictEntity>> {
        return object : NetworkBoundResource<PredictEntity, PredictResponse>(appExecutors){
            override fun loadFromDB(): LiveData<PredictEntity> =
                localDataSource.getResult(file.name)

            override fun shouldFetch(data: PredictEntity?): Boolean =
                data?.id == null

            override fun createCall(): LiveData<ApiResponse<PredictResponse>> =
                remoteDataSource.getPredictionResult(context, file, body)

            override fun saveCallResult(data: PredictResponse) {
                val result = PredictEntity(
                    id = file.name,
                    file = data.file.toString(),
                    result = data.result,
//                    akurasi = data.akurasi,
//                    message = data.message
                )
                localDataSource.insertResult(result)
            }

        }.asLiveData()
    }

    companion object {
        @Volatile
        private var instance: PredictRepository? = null

        fun getInstance(
            remoteData: RemoteDataSource,
            localData: LocalDataSource,
            appExecutors: AppExecutors
        ): PredictRepository =
            instance ?: synchronized(this) {
                instance ?: PredictRepository(remoteData, localData, appExecutors).apply {
                    instance = this
                }
            }
    }
}