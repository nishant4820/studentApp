package com.nishant4820.studentapp

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.models.NoticeResponse
import com.nishant4820.studentapp.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    var noticeResponse: MutableLiveData<NetworkResult<NoticeResponse>> = MutableLiveData()

    fun getAllNotices() = viewModelScope.launch {
        getAllNoticesSafeCall()
    }

    private suspend fun getAllNoticesSafeCall() {
        noticeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllNotices()
                noticeResponse.value = handleAllNoticesResponse(response)
            } catch (e: Exception) {
                noticeResponse.value = NetworkResult.Error("Notices not found.")
            }
        } else {
            noticeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun handleAllNoticesResponse(response: Response<NoticeResponse>): NetworkResult<NoticeResponse>? {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }

            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }

            response.body()!!.isEmpty() -> {
                return NetworkResult.Error("Notices not found.")
            }

            response.isSuccessful -> {
                val notices = response.body()
                return NetworkResult.Success(notices!!)
            }

            else -> {
                return NetworkResult.Error(response.message())
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