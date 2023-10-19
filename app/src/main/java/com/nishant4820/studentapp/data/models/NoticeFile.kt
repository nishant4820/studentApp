package com.nishant4820.studentapp.data.models

import com.google.gson.annotations.SerializedName

data class NoticeFile(
    @SerializedName("file_name")
    val fileName: String,
    @SerializedName("file_url")
    val fileUrl: String,
    @SerializedName("path")
    val path: String
)
