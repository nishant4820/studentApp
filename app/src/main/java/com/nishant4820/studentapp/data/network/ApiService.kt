package com.nishant4820.studentapp.data.network

import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.SettingsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.QueryMap

interface ApiService {

    @GET("eventservice/notice/getall")
    suspend fun getAllNotices(@QueryMap queries: Map<String, String>): Response<NoticeResponse>

    @GET("eventservice/student/settings")
    suspend fun getSettings(@Header("Authorization") token: String): Response<SettingsResponse>
}