package com.dicoding.anarki.ui.home

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.anarki.data.PredictRepository
import com.dicoding.anarki.data.source.local.entity.PredictEntity
import com.dicoding.anarki.data.source.remote.ApiResponse
import com.dicoding.anarki.data.source.remote.response.PredictResponse
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.network.ConfigNetwork
import com.dicoding.anarki.vo.Resource
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeViewModel(private val predictRepository: PredictRepository) : ViewModel() {

    private val listDetails = MutableLiveData<PredictResponse>()

    fun getPredictionResult(context: Context, file: File, body: UploadRequest): LiveData<Resource<PredictEntity>> =
        predictRepository.getResult(context, file, body)

//    fun getPredictionResult(context: Context, file: File, body: UploadRequest): LiveData<PredictResponse> {
//        ConfigNetwork.getRetrofit().uploadImage(
//            MultipartBody.Part.createFormData(
//                "file",
//                file.name,
//                body
//            ),
//        ).enqueue(object : Callback<PredictResponse> {
//            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
//                val data = PredictResponse(
//                    image = "https://image.flaticon.com/icons/png/512/675/675564.png",
//                    pecandu = null,
//                    akurasi = null
//                )
//                listDetails.postValue(data)
//                Toast.makeText(context, "Server on Failure", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onResponse(
//                call: Call<PredictResponse>,
//                response: Response<PredictResponse>
//            ) {
//                if (response.isSuccessful) {
//                    val result = response.body()
//                    val data = PredictResponse(
//                        image = result?.image,
//                        pecandu = result?.pecandu,
//                        akurasi = result?.akurasi,
//                        message = result?.message.toString()
//                    )
//                    listDetails.postValue(data)
//                    if (body.toString() == "[]") {
//                        Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        })
//        return listDetails
//    }
}