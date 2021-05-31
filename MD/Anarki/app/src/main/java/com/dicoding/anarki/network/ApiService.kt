package com.dicoding.anarki.network

import com.dicoding.anarki.data.source.remote.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("detect")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<PredictResponse>
}