package com.sayed.campusdock.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient
{
    //private const val BASE_URL= "http://campusdock-api.vidvault.me:8082/"
    private const val BASE_URL= "http://192.168.1.9:8081/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    }
}