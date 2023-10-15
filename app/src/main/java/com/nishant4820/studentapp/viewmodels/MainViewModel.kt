package com.nishant4820.studentapp.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.NoticesEntity
import com.nishant4820.studentapp.data.models.NoticeResponse
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
        noticesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getAllNotices(queries)
                noticesResponse.value = handleAllNoticesResponse(response)

                val notices = noticesResponse.value!!.data
                if(notices != null) {
                    offlineCacheNotices(notices)
                }

            } catch (e: Exception) {
                noticesResponse.value = NetworkResult.Error("Notices not found.")
            }
        } else {
            noticesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private fun offlineCacheNotices(notices: NoticeResponse) {
        val noticesEntity = NoticesEntity(notices)
        insertNotices(noticesEntity)
    }

    private fun handleAllNoticesResponse(response: Response<NoticeResponse>): NetworkResult<NoticeResponse> {
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }

            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }

            response.body()!!.data.isEmpty() -> {
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