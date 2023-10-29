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
    @SerializedName("enrollment")
    val enrollment: Long,
    @SerializedName("password")
    val password: String
)

data class LoginFormState(
    val enrollmentError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)
