package com.nishant4820.studentapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nishant4820.studentapp.data.models.LoginResponse
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.SettingsResponse
import com.nishant4820.studentapp.data.models.StudentProfileResponse
import com.nishant4820.studentapp.data.models.StudentResultResponse

class MyTypeConverter {


    @TypeConverter
    fun noticesToString(foodRecipe: NoticeResponse): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToNotices(data: String): NoticeResponse {
        val listType = object : TypeToken<NoticeResponse>() {}.type
        return gson.fromJson(data, listType)
    }

//    @TypeConverter
//    fun dataToString(data: NoticeData): String {
//        return gson.toJson(data)
//    }
//
//    @TypeConverter
//    fun stringToData(data: String): NoticeData {
//        val listType = object : TypeToken<NoticeData>() {}.type
//        return gson.fromJson(data, listType)
//    }

    @TypeConverter
    fun settingsToString(settings: SettingsResponse): String {
        return gson.toJson(settings)
    }

    @TypeConverter
    fun stringToSettings(data: String): SettingsResponse {
        val listType = object : TypeToken<SettingsResponse>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun resultsToString(results: StudentResultResponse): String {
        return gson.toJson(results)
    }

    @TypeConverter
    fun stringToResults(data: String): StudentResultResponse {
        val listType = object : TypeToken<StudentResultResponse>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun profileToString(profile: StudentProfileResponse): String {
        return gson.toJson(profile)
    }

    @TypeConverter
    fun stringToProfile(data: String): StudentProfileResponse {
        val listType = object : TypeToken<StudentProfileResponse>() {}.type
        return gson.fromJson(data, listType)
    }

    companion object {
        private var gson = Gson()

        fun loginResponseToString(loginResponse: LoginResponse): String {
            return gson.toJson(loginResponse)
        }

        fun stringToLoginResponse(data: String): LoginResponse {
            val listType = object : TypeToken<LoginResponse>() {}.type
            return gson.fromJson(data, listType)
        }

//        fun noticeDataToString(noticeData: NoticeData): String {
//            return gson.toJson(noticeData)
//        }

        fun stringToNoticeData(data: String): NoticeData {
            val listType = object : TypeToken<NoticeData>() {}.type
            return gson.fromJson(data, listType)
        }
    }

}