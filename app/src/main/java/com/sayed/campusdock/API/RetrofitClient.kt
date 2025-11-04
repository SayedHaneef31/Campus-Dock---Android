package com.sayed.campusdock.API

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.sayed.campusdock.Adaptor.LocalDateTimeAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
object RetrofitClient {
    //    private const val BASE_URL= "https://api.campusdock.live/"
//    private const val BASE_URL= "http:/192.168.29.10:8081/"
    //private const val BASE_URL= "http://172.16.45.158:8081/"      //kc wifi
    private const val BASE_URL= "http://192.168.1.33:8081/"

    val instance: ApiService by lazy {

        // Correctly create and configure the Gson instance
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)) // Pass the custom gson object here
            .build()
            .create(ApiService::class.java)

    }

}