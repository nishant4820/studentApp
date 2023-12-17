package com.nishant4820.studentapp.data.models


import com.google.gson.annotations.SerializedName

data class StudentResultResponse(
    @SerializedName("student_name")
    val studentName: String,
    @SerializedName("enrollment_number")
    val enrollmentNumber: String,
    @SerializedName("college")
    val college: String,
    @SerializedName("college_code")
    val collegeCode: Int,
    @SerializedName("batch")
    val batch: Int,
    @SerializedName("branch")
    val branch: String,
    @SerializedName("branch_code")
    val branchCode: String,
    @SerializedName("result")
    val result: Result,
    @SerializedName("overall_percentage")
    val overallPercentage: Double,
    @SerializedName("overall_cgpa")
    val overallCgpa: Double,
    @SerializedName("total_credits")
    val totalCredits: Int,
    @SerializedName("total_marks")
    val totalMarks: Int,
    @SerializedName("subject_count")
    val subjectCount: Int
)

data class Result(
    @SerializedName("8")
    val sem8: SemesterResult?,
    @SerializedName("7")
    val sem7: SemesterResult?,
    @SerializedName("6")
    val sem6: SemesterResult?,
    @SerializedName("5")
    val sem5: SemesterResult?,
    @SerializedName("4")
    val sem4: SemesterResult?,
    @SerializedName("3")
    val sem3: SemesterResult?,
    @SerializedName("2")
    val sem2: SemesterResult?,
    @SerializedName("1")
    val sem1: SemesterResult?
)

data class SemesterResult(
    @SerializedName("semester")
    val semester: Int,
    @SerializedName("subjects")
    val subjects: List<SubjectResult>,
    @SerializedName("cgpa")
    val cgpa: Double,
    @SerializedName("total_credits")
    val totalCredits: Int,
    @SerializedName("percentage")
    val percentage: Double
)

data class SubjectResult(
    @SerializedName("subject_name")
    val subjectName: String,
    @SerializedName("subject_code")
    val subjectCode: String,
    @SerializedName("subject_credit")
    val subjectCredit: Int,
    @SerializedName("grade")
    val grade: String,
    @SerializedName("internal_marks")
    val internalMarks: Int,
    @SerializedName("external_marks")
    val externalMarks: Int,
    @SerializedName("total_marks")
    val totalMarks: Int
)