package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class NoticeResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val data: ArrayList<NoticeResponseItem>
)

data class NoticeResponseItem(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("Links")
    val links: List<Link>,
    @SerializedName("name")
    val name: String,
    @SerializedName("noticeFile")
    val noticeFile: String,
    @SerializedName("noticeType")
    val noticeType: String,
    @SerializedName("society")
    val society: String,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class Link(
    @SerializedName("_id")
    val id: String,
    @SerializedName("LinkName")
    val linkName: String,
    @SerializedName("url")
    val url: String
)