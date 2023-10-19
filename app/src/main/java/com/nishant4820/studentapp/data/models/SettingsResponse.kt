package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class SettingsResponse(
    @SerializedName("society_details")
    val societyDetails: SocietyDetails,
    @SerializedName("society_list")
    val societyList: List<Society>
)

data class SocietyDetails(
    @SerializedName("isAdmin")
    val isAdmin: Boolean,
    @SerializedName("society")
    val society: Society
)
