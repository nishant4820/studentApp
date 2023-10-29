package com.nishant4820.studentapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nishant4820.studentapp.data.models.LoginResponse
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.SettingsResponse

class MyTypeConverter {


    @TypeConverter
    fun foodRecipeToString(foodRecipe: NoticeResponse): String {
        return gson.toJson(foodRecipe)
    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): NoticeResponse {
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

    companion object {
        private var gson = Gson()

        fun loginResponseToString(loginResponse: LoginResponse): String {
            return gson.toJson(loginResponse)
        }

        fun stringToLoginResponse(data: String): LoginResponse {
            val listType = object : TypeToken<LoginResponse>() {}.type
            return gson.fromJson(data, listType)
        }
    }

}