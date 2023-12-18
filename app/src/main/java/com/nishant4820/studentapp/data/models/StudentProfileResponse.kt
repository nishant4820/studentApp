package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class StudentProfileResponse(
    @SerializedName("isSocietyAdmin")
    val isSocietyAdmin: Boolean,
    @SerializedName("_id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("enrollment")
    val enrollment: Long,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("batch")
    val batch: Int,
    @SerializedName("section")
    val section: String,
    @SerializedName("branch")
    val branch: String,
    @SerializedName("society")
    val society: Society?,
    @SerializedName("address")
    val address: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)