package com.nishant4820.studentapp.data.models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

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

@Parcelize
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
    val links: @RawValue List<Link>,
    @SerializedName("name")
    val name: String,
    @SerializedName("noticeFile")
    val noticeFile: @RawValue NoticeFile,
    @SerializedName("noticeType")
    val noticeType: String,
    @SerializedName("society")
    val society: @RawValue Society,
    @SerializedName("studentId")
    val studentId: String,
    @SerializedName("updatedAt")
    val updatedAt: String
) : Parcelable

@Parcelize
data class Link(
    @SerializedName("_id")
    val id: String,
    @SerializedName("LinkName")
    val linkName: String,
    @SerializedName("url")
    val url: String
) : Parcelable

@Parcelize
data class Image(
    @SerializedName("image_name")
    val imageName: String,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("path")
    val path: String
) : Parcelable