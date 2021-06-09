package com.dicoding.anarki.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.anarki.data.PredictRepository
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.data.source.remote.response.PredictResponse
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.vo.Resource
import java.io.File

class HomeViewModel(private val predictRepository: PredictRepository) : ViewModel() {

    fun getPredictionResult(
        context: Context,
        file: File,
        body: UploadRequest
    ): LiveData<Resource<PredictEntity>> =
        predictRepository.getResult(context, file, body)

}