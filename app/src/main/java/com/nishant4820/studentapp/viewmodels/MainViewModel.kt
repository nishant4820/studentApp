package com.nishant4820.studentapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.NoticesEntity
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_NO_INTERNET
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_UNKNOWN
import com.nishant4820.studentapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /* ROOM DATABASE */

    val readNotices: LiveData<List<NoticesEntity>> = repository.local.readNotices().asLiveData()

    private fun insertNotices(noticesEntity: NoticesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertNotices(noticesEntity)
        }


    /* RETROFIT */

    var noticesResponse: MutableLiveData<NetworkResult<NoticeResponse>> = MutableLiveData()

    fun getAllNotices(queries: HashMap<String, String>) = viewModelScope.launch {
        getAllNoticesSafeCall(queries)
    }

    private suspend fun getAllNoticesSafeCall(queries: HashMap<String, String>) {
        noticesResponse.value =
            NetworkResult.Loading(NETWORK_RESULT_MESSAGE_LOADING, NETWORK_RESULT_STATUS_LOADING)
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllNotices(queries)
                noticesResponse.value = handleAllNoticesResponse(response)

                val notices = noticesResponse.value!!.data
                if (notices != null) {
                    offlineCacheNotices(notices)
                }

            } catch (e: Exception) {
                Log.d(
                    LOG_TAG,
                    "Main Vew Model: getAllNoticesSafeCall, exception message: ${e.message}"
                )
                if (e.message.toString().contains("timeout")) {
                    noticesResponse.value = NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_TIMEOUT,
                        NETWORK_RESULT_STATUS_TIMEOUT
                    )
                } else {
                    noticesResponse.value =
                        NetworkResult.Error(
                            NETWORK_RESULT_MESSAGE_UNKNOWN,
                            NETWORK_RESULT_STATUS_UNKNOWN
                        )
                }
            }
        } else {
            Log.d(LOG_TAG, "Main Vew Model: getAllNoticesSafeCall, no internet actually")
            noticesResponse.value = NetworkResult.Error(
                NETWORK_RESULT_MESSAGE_NO_INTERNET,
                NETWORK_RESULT_STATUS_NO_INTERNET
            )
        }
    }

    private fun offlineCacheNotices(notices: NoticeResponse) {
        val noticesEntity = NoticesEntity(notices)
        insertNotices(noticesEntity)
    }

    private fun handleAllNoticesResponse(response: Response<NoticeResponse>): NetworkResult<NoticeResponse> {
        Log.d(
            LOG_TAG,
            "Main Vew Model: handleAllNoticesResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(NETWORK_RESULT_MESSAGE_TIMEOUT, response.code())
            }

            (response.code() == 201 || response.body()!!.message == "Notices are empty" || response.body()!!.data.isEmpty()) -> {
                return NetworkResult.Error(NETWORK_RESULT_MESSAGE_NO_RESULTS, response.code())
            }

            response.isSuccessful -> {
                val notices = response.body()
                return NetworkResult.Success(notices!!, response.message(), response.code())
            }

            else -> {
                return NetworkResult.Error(response.message(), response.code())
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}