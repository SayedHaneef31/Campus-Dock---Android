package com.sayed.campusdock

import com.sayed.campusdock.API.ApiService

object RetrofitClient
{
    //private const val BASE_URL= "http://campusdock-api.vidvault.me:8082/"
    private const val BASE_URL= "http://192.168.1.9:8081/"

    val instance: ApiService by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

    }
}