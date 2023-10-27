package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class NoticeResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("data")
    val `data`: List<NoticeData>
)

data class NoticeData(
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
    val noticeFile: NoticeFile,
    @SerializedName("noticeType")
    val noticeType: String,
    @SerializedName("society")
    val society: Society,
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

data class Image(
    @SerializedName("image_name")
    val imageName: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("path")
    val path: String
)