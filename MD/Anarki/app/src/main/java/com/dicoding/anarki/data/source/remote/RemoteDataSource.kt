package com.dicoding.anarki.data.source.remote

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.anarki.network.ConfigNetwork
import com.dicoding.anarki.network.UploadRequest
import com.dicoding.anarki.data.source.remote.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RemoteDataSource {

    fun getPredictionResult(context: Context, file: File, body: UploadRequest): LiveData<ApiResponse<PredictResponse>> {
        val listDetails = MutableLiveData<ApiResponse<PredictResponse>>()
        ConfigNetwork.getRetrofit().uploadImage(
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                body
            ),
        ).enqueue(object : Callback<PredictResponse> {
            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                val data = PredictResponse(
                    image = "https://image.flaticon.com/icons/png/512/675/675564.png",
                    pecandu = null,
                    akurasi = 0.0,
                    message = ""
                )
                listDetails.postValue(ApiResponse.success(data))
                Toast.makeText(context, "Server on Failure", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val data = PredictResponse(
                        image = result?.image,
                        pecandu = result?.pecandu,
                        akurasi = result?.akurasi,
                        message = result?.message.toString()
                    )
                    listDetails.postValue(ApiResponse.success(data))
                    if (body.toString() == "[]") {
                        Toast.makeText(context, "Data not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        return listDetails
    }
    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null

        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource().apply { instance = this }
            }
    }
}