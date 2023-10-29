package com.nishant4820.studentapp.data

import com.nishant4820.studentapp.data.models.LoginRequestBody
import com.nishant4820.studentapp.data.models.LoginResponse
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.models.SettingsResponse
import com.nishant4820.studentapp.data.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun login(loginRequestBody: LoginRequestBody): Response<LoginResponse> {
        return apiService.login(loginRequestBody)
    }

    suspend fun getAllNotices(queries: HashMap<String, String>): Response<NoticeResponse> {
        return apiService.getAllNotices(queries)
    }

    suspend fun getSettings(token: String): Response<SettingsResponse> {
        return apiService.getSettings(token)
    }

}