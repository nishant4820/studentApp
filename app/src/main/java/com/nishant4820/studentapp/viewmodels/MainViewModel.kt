package com.nishant4820.studentapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.settings.SettingsEntity
import com.nishant4820.studentapp.data.models.SettingsResponse
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_TOKEN
import com.nishant4820.studentapp.utils.NetworkResult
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    /* ROOM DATABASE */

    val readSettings: LiveData<List<SettingsEntity>> = repository.local.readSettings().asLiveData()

    private fun insertSettings(settingsEntity: SettingsEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertSettings(settingsEntity)
        }


    /* RETROFIT */


    private val _settingsResponse = MutableLiveData<NetworkResult<SettingsResponse>>()
    val settingsResponse: LiveData<NetworkResult<SettingsResponse>> = _settingsResponse

    fun getSettings() =
        viewModelScope.launch {
            getSettingsSafeCall()
        }

    private suspend fun getSettingsSafeCall() {
        _settingsResponse.value =
            NetworkResult.Loading(NETWORK_RESULT_MESSAGE_LOADING, NETWORK_RESULT_STATUS_LOADING)
        try {
            val token = Hawk.get<String>(PREFERENCES_TOKEN)
            val response = repository.remote.getSettings(token)
            _settingsResponse.value = handleSettingsResponse(response)

            if (settingsResponse.value is NetworkResult.Success) {
                val settings = settingsResponse.value!!.data
                if (settings != null) {
                    saveSettingsInRoom(settings)
                }
            }

        } catch (e: Exception) {
            Log.d(
                LOG_TAG,
                "Main Vew Model: getSettingsSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                _settingsResponse.value = NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                _settingsResponse.value =
                    NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_UNKNOWN,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun saveSettingsInRoom(settings: SettingsResponse) {
        val settingsEntity = SettingsEntity(settings)
        insertSettings(settingsEntity)
    }

    private fun handleSettingsResponse(response: Response<SettingsResponse>): NetworkResult<SettingsResponse> {
        Log.d(
            LOG_TAG,
            "Main Vew Model: handleSettingsResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(NETWORK_RESULT_MESSAGE_TIMEOUT, response.code())
            }

            response.isSuccessful -> {
                val settings = response.body()
                NetworkResult.Success(settings!!, response.message(), response.code())
            }

            else -> {
                NetworkResult.Error(response.message(), response.code())
            }
        }
    }

}