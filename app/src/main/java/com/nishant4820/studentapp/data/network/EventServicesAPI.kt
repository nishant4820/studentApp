package com.nishant4820.studentapp.data.network

import com.nishant4820.studentapp.data.models.NoticeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface EventServicesAPI {

    @GET("/eventservice/notice/getall")
    suspend fun getAllNotices(@QueryMap queries: Map<String, String>): Response<NoticeResponse>
}