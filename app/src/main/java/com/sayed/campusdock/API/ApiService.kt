package com.sayed.campusdock.API

import com.sayed.campusdock.Data.Canteen.Canteen
import com.sayed.campusdock.Data.Canteen.Cart
import com.sayed.campusdock.Data.CollegeSpinner
import com.sayed.campusdock.Data.CreateUser
import com.sayed.campusdock.Data.Canteen.MenuItem
import com.sayed.campusdock.Data.Auth.OtpResponse
import com.sayed.campusdock.Data.Auth.OtpVerifyResponse
import com.sayed.campusdock.Data.Room.CartItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService
{



    // -------------------  AUTH API  -------------------//
    @POST("api/v1/auth/sendOTP")
    suspend fun sendOTP(@Body request: CreateUser): Response<OtpResponse>                           //Sending login otp

    @POST("api/v1/auth/verifyOTP")
    suspend fun verifyOTP(@Query("email") email: String,
                          @Query("otp") otp: String): Response<OtpVerifyResponse>           //Verifying login otp
    // -------------------  AUTH API  -------------------//





    // -------------------  CANTEEN API  -------------------//
    @GET("api/v1/colleges/{collegeId}/canteens")
    suspend fun getCanteens(@Path("collegeId") collegeId: String): List<Canteen>            // Display all the canteens of specific college by id

    @GET("api/v1/menuItems/canteens/{canteenId}")
    suspend fun getMenuItems(@Path("canteenId") canteenId: String): List<MenuItem>
    // -------------------  CANTEEN API  -------------------//




    // -------------------  CART API  -------------------//
    @GET("api/v1/cart")
    suspend fun getFullCart(
        @Query("userId") userId: String
    ): Response<Cart>                                                                                      // Display full cart of user
//    Response from backend
//    {
//        "cartId": "30490ec5-6e80-4671-ba80-4b15f1c262e5",
//        "canteenId": "b449a27a-0da2-45d1-871a-eb077c7aaa78",
//        "canteenName": "Cocococo cola",
//        "items": [],      <----- here we will get all the items in cart
//        "totalAmount": 0.0
//    }

    @POST("api/v1/cart/add")
    suspend fun addItemToCart(
        @Query("userId") userId: String,
        @Query("menuItemId") menuItemId: String,
        @Query("quantity") quantity: Int
    ): Response<Cart>                                                                                       // Add item to cart

    @POST("api/v1/cart/update")
    suspend fun updateItemQuantity(
        @Query("userId") userId: String,
        @Query("menuItemId") menuItemId: String,
        @Query("quantity") quantity: Int
    ): Response<Cart>                                                                                       // Update item quantity

    @DELETE("api/v1/cart/remove")
    suspend fun removeItemFromCart(
        @Query("userId") userId: String,
        @Query("menuItemId") menuItemId: String
    ): Response<Cart>                                                                                       // Remove item from cart

    @POST("api/v1/cart/sync")
    suspend fun syncCart(
        @Query("userId") userId: String,
        @Body cartItems: List<CartItem>                                                                     // Sync cart with backend
    ): Response<Unit> // We only care if the call was successful, no data needs to be returned
    // -------------------  CART API  -------------------//




    // -------------------  COLLEGE API  -------------------//
    @GET("api/v1/colleges/name")
    suspend fun getCollegesName(): List<CollegeSpinner>                                             // Display all the colleges in login spinner
    // -------------------  COLLEGE API  -------------------//


}