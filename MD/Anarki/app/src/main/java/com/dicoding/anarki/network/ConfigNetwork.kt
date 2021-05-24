package com.dicoding.anarki.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ConfigNetwork {
    companion object {
        fun getRetrofit() : ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://34.101.129.145:8000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}