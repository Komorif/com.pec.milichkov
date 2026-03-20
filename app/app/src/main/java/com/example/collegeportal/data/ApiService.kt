package com.example.collegeportal.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("user-profile")
    suspend fun getProfile(): Response<User>

    @GET("all-data")
    suspend fun getAllData(): Response<AllDataResponse>

    @Multipart
    @POST("update-avatar")
    suspend fun updateAvatar(
        @Part avatar: MultipartBody.Part
    ): Response<AvatarResponse>

    @POST("update-avatar")
    suspend fun updateAvatarPreset(
        @Body request: Map<String, Int>
    ): Response<AvatarResponse>
}
