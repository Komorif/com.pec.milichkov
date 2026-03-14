package com.example.collegeportal.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<User>

    @GET("user-profile")
    suspend fun getProfile(@Query("user_token") token: String): Response<User>

    @Multipart
    @POST("update-avatar")
    suspend fun updateAvatar(
        @Part("user_token") token: RequestBody,
        @Part avatar: MultipartBody.Part
    ): Response<AvatarResponse>
}
