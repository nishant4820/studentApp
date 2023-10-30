package com.nishant4820.studentapp.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoticeFile(
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("file_url")
    val fileUrl: String,
    @SerializedName("path")
    val path: String
) : Parcelable
