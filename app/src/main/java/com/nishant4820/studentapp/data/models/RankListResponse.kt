package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class RankListResponse(
    @SerializedName("data")
    val `data`: List<RankListData>,
    @SerializedName("semester")
    val semester: Int,
    @SerializedName("branch_id")
    val branchId: String,
    @SerializedName("batch")
    val batch: String
)

data class RankListData(
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("cgpa")
    val cgpa: Double,
    @SerializedName("enrollment_number")
    val enrollmentNumber: Long,
    @SerializedName("student_name")
    val studentName: String,
    @SerializedName("total_marks")
    val totalMarks: Int,
    @SerializedName("outof")
    val outOf: Int,
    @SerializedName("percentage")
    val percentage: Int
)