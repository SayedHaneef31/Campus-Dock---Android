package com.sayed.campusdock.API

import com.sayed.campusdock.Data.Canteen
import com.sayed.campusdock.Data.CollegeSpinner
import com.sayed.campusdock.Data.CreateUser
import com.sayed.campusdock.Data.MenuItem
import com.sayed.campusdock.Data.OtpResponse
import com.sayed.campusdock.Data.OtpVerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService
{

    @GET("api/v1/colleges/caf0ae86-27da-4243-bf75-431b83ecefca/canteens")
    suspend fun getCanteens(): List<Canteen>      // Display all the canteens of specific college by id

    @GET("api/v1/colleges/name")
    suspend fun getCollegesName(): List<CollegeSpinner>      // Display all the colleges in login spinner

    @POST("api/v1/auth/sendOTP")
    suspend fun sendOTP(@Body request: CreateUser): Response<OtpResponse>      //Sending login otp

    @POST("api/v1/auth/verifyOTP")
    suspend fun verifyOTP(@Query("email") email: String,
                          @Query("otp") otp: String): Response<OtpVerifyResponse>      //Verifying login otp


    @GET("api/v1/menuItems/canteens/{canteenId}")
    suspend fun getMenuItems(@Path("canteenId") canteenId: String): List<MenuItem>

}