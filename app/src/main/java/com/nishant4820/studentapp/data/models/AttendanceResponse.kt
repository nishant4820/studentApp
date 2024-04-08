package com.nishant4820.studentapp.data.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class AttendanceResponse : ArrayList<AttendanceItem>()

@Parcelize
data class AttendanceItem(
    @SerializedName("subject_name")
    @Expose
    var subjectName: String? = null,

    @SerializedName("present")
    @Expose
    var present: Int? = null,

    @SerializedName("total")
    @Expose
    var total: Int? = null
) : Parcelable