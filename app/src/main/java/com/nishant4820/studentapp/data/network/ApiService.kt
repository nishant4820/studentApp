package com.nishant4820.studentapp.data.network

import com.nishant4820.studentapp.data.models.LoginRequestBody
import com.nishant4820.studentapp.data.models.LoginResponse
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.SettingsResponse
import com.nishant4820.studentapp.data.models.StudentProfileResponse
import com.nishant4820.studentapp.data.models.StudentResultResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface ApiService {

    @POST("events_service/student/login")
    suspend fun login(@Body loginRequestBody: LoginRequestBody): Response<LoginResponse>

    @GET("events_service/student/settings")
    suspend fun getSettings(@Header("Authorization") token: String): Response<SettingsResponse>

    @GET("events_service/notice/getall")
    suspend fun getAllNotices(@QueryMap queries: Map<String, String>): Response<NoticeResponse>

    @POST("events_service/notice/create")
    suspend fun postNotice(@Header("Authorization") token: String, @Body noticeRequestBody: NoticeData): Response<NoticeData>

    @GET("events_service/student/profile")
    suspend fun getStudentProfile(@Header("Authorization") token: String): Response<StudentProfileResponse>

    @GET("result_service/getrollnumdata/")
    suspend fun getStudentResult(@QueryMap queries: Map<String, String>): Response<StudentResultResponse>

}