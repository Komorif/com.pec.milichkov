package com.example.collegeportal.data

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("user_token") val userToken: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val surname: String,
    @SerializedName("educational_organization") val educationalOrganization: String,
    @SerializedName("date_of_issue") val dateOfIssue: String,
    @SerializedName("organization_level") val organizationLevel: String,
    val direction: String,
    val course: String
)

data class LoginRequest(
    @SerializedName("user_token") val userToken: String
)
