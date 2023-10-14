package com.nishant4820.studentapp.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.NoticeResponseItem

class NoticesTypeConverter {

    var gson = Gson()

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
//    fun dataToString(data: NoticeResponseItem): String {
//        return gson.toJson(data)
//    }
//
//    @TypeConverter
//    fun stringToData(data: String): NoticeResponseItem {
//        val listType = object : TypeToken<NoticeResponseItem>() {}.type
//        return gson.fromJson(data, listType)
//    }

}