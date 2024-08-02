package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("enrollment")
    val enrollment: Long,
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("token")
    val token: String
)

data class LoginRequestBody(
    @SerializedName("access_token")
    val accessToken: String
)