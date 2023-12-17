package com.nishant4820.studentapp.data

import com.nishant4820.studentapp.data.models.LoginRequestBody
import com.nishant4820.studentapp.data.models.LoginResponse
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.SettingsResponse
import com.nishant4820.studentapp.data.models.StudentResultResponse
import com.nishant4820.studentapp.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun login(loginRequestBody: LoginRequestBody): Response<LoginResponse> {
        return apiService.login(loginRequestBody)
    }

    suspend fun getSettings(token: String): Response<SettingsResponse> {
        return apiService.getSettings(token)
    }

    suspend fun getAllNotices(queries: HashMap<String, String>): Response<NoticeResponse> {
        return apiService.getAllNotices(queries)
    }

    suspend fun postNotice(token: String, noticeRequestBody: NoticeData): Response<NoticeData> {
        return apiService.postNotice(token, noticeRequestBody)
    }

    suspend fun getStudentResult(queries: HashMap<String, String>): Response<StudentResultResponse> {
        return apiService.getStudentResult(queries)
    }

}