package com.sayed.campusdock.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient
{
//    private const val BASE_URL= "https://api.campusdock.live/"
//    private const val BASE_URL= "http:/192.168.29.10:8081/"
    //private const val BASE_URL= "http://172.16.45.158:8081/"      //kc wifi
    private const val BASE_URL= "http://192.168.1.7:8081/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    }

}