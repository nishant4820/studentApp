package com.nishant4820.studentapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.notices.NoticesEntity
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.utils.Constants
import com.nishant4820.studentapp.utils.Constants.DEFAULT_LIMIT
import com.nishant4820.studentapp.utils.Constants.DEFAULT_OFFSET
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ID
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_IS_UPLOADED_BY_ME
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_SOCIETY
import com.nishant4820.studentapp.utils.Constants.QUERY_LIMIT
import com.nishant4820.studentapp.utils.Constants.QUERY_OFFSET
import com.nishant4820.studentapp.utils.Constants.QUERY_SOCIETY
import com.nishant4820.studentapp.utils.Constants.QUERY_STUDENT_ID
import com.nishant4820.studentapp.utils.NetworkResult
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NoticesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    var networkStatus = false


    /* ROOM DATABASE */

    val readNotices: LiveData<List<NoticesEntity>> = repository.local.readNotices().asLiveData()

    private fun insertNotices(noticesEntity: NoticesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertNotices(noticesEntity)
        }


    /* RETROFIT */


    private val _noticesResponse = MutableLiveData<NetworkResult<NoticeResponse>>()
    val noticesResponse: LiveData<NetworkResult<NoticeResponse>> = _noticesResponse

    fun getAllNotices(queries: HashMap<String, String>) {
        viewModelScope.launch {
            getAllNoticesSafeCall(queries)
        }
    }

    private suspend fun getAllNoticesSafeCall(queries: HashMap<String, String>) {
        _noticesResponse.value =
            NetworkResult.Loading(
                Constants.NETWORK_RESULT_MESSAGE_LOADING,
                Constants.NETWORK_RESULT_STATUS_LOADING
            )
        try {
            val response = repository.remote.getAllNotices(queries)
            _noticesResponse.value = handleAllNoticesResponse(response)
            if (noticesResponse.value is NetworkResult.Success) {
                val notices = noticesResponse.value!!.data
                if (notices != null) {
                    offlineCacheNotices(notices)
                }
            }

        } catch (e: Exception) {
            Log.d(
                Constants.LOG_TAG,
                "Notices Vew Model: getAllNoticesSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                _noticesResponse.value = NetworkResult.Error(
                    Constants.NETWORK_RESULT_MESSAGE_TIMEOUT,
                    Constants.NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                _noticesResponse.value =
                    NetworkResult.Error(
                        Constants.NETWORK_RESULT_MESSAGE_UNKNOWN,
                        Constants.NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun offlineCacheNotices(notices: NoticeResponse) {
        val noticesEntity = NoticesEntity(notices)
        insertNotices(noticesEntity)
    }

    private fun handleAllNoticesResponse(response: Response<NoticeResponse>): NetworkResult<NoticeResponse> {
        Log.d(
            Constants.LOG_TAG,
            "Notices Vew Model: handleAllNoticesResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(Constants.NETWORK_RESULT_MESSAGE_TIMEOUT, response.code())
            }

            response.isSuccessful -> {
                val notices = response.body()!!
                val message =
                    if (notices.data.isEmpty()) Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS else response.message()
                NetworkResult.Success(notices, message, response.code())
            }

            else -> {
                NetworkResult.Error(response.message(), response.code())
            }
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        if (Hawk.contains(PREFERENCES_SOCIETY)) {
            queries[QUERY_SOCIETY] = Hawk.get(PREFERENCES_SOCIETY)
        }

        if (Hawk.contains(PREFERENCES_IS_UPLOADED_BY_ME)) {
            val studentId = Hawk.get<String>(PREFERENCES_ID)
            queries[QUERY_STUDENT_ID] = studentId
        }

//        queries[QUERY_OFFSET] = DEFAULT_OFFSET
//        queries[QUERY_LIMIT] = DEFAULT_LIMIT

        return queries
    }

}