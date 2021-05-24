package com.dicoding.anarki.network

import com.dicoding.anarki.model.PredictResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    // @MultiPart for Data Sending(Image) ??
    /*@Multipart
    @POST("detect/")
    fun getPrediction(@Query("q") file: String): Call<PredictResponse>
    */

    @Multipart
    @POST("detect")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<PredictResponse>
}