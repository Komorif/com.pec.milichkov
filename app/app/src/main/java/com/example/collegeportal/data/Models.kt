package com.example.collegeportal.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("user_token") val userToken: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val surname: String,
    @SerializedName("educational_organization") val educationalOrganization: String,
    val group: String,
    val course: String,
    val avatar: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

data class LoginRequest(
    @SerializedName("user_token") val userToken: String
)

data class AvatarResponse(
    val message: String,
    val avatar: String,
    @SerializedName("avatar_url") val avatarUrl: String
)
