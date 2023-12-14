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
    @SerializedName("status")
    val status: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("Links")
    val links: @RawValue List<Link>? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("noticeFile")
    val noticeFile: @RawValue NoticeFile,
    @SerializedName("noticeType")
    val noticeType: String,
    @SerializedName("society")
    val society: @RawValue Society? = null,
    @SerializedName("studentId")
    val studentId: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
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

data class NoticeFormState(
    val enrollmentError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)