package com.dicoding.anarki.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.vo.Resource
import java.io.File

interface PredictDataSource {

    fun getResult(context: Context, file: File, body: UploadRequest): LiveData<Resource<PredictEntity>>
}