package com.sayed.campusdock.API

import com.sayed.campusdock.Data.Canteen
import retrofit2.http.GET

interface ApiService
{
    @GET("api/v1/colleges/caf0ae86-27da-4243-bf75-431b83ecefca/canteens")
    suspend fun getCanteens(): List<Canteen>
}