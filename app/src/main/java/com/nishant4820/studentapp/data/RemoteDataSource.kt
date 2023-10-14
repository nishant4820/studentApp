package com.nishant4820.studentapp.data

import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.data.network.EventServicesAPI
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val eventServicesAPI: EventServicesAPI
) {

    suspend fun getAllNotices(): Response<NoticeResponse> {
        return eventServicesAPI.getAllNotices()
    }

}