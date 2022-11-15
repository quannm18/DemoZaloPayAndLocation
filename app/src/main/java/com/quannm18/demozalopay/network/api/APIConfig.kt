package com.quannm18.demozalopay.network.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIConfig {
    companion object {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        const val BASE_URL = "https://api.cloudinary.com/"

        //        val gson = GsonBuilder().setLenient().create()
        private val builder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(BASE_URL)

        val retrofit = builder.build()

        val apiService: APIService = retrofit.create(APIService::class.java)
    }
}