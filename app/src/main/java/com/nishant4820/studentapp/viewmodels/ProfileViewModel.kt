package com.nishant4820.studentapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.profile.ProfileEntity
import com.nishant4820.studentapp.data.models.StudentProfileResponse
import com.nishant4820.studentapp.utils.Constants
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_UNKNOWN
import com.nishant4820.studentapp.utils.NetworkResult
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    /* ROOM DATABASE */

    val readProfile: LiveData<List<ProfileEntity>> = repository.local.readProfile().asLiveData()

    private fun insertProfile(profileEntity: ProfileEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertProfile(profileEntity)
        }

    fun clearAllTables() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.clearAllTables()
        }
    }


    /* RETROFIT */

    private val _profileResponse = MutableLiveData<NetworkResult<StudentProfileResponse>>()
    val profileResponse: LiveData<NetworkResult<StudentProfileResponse>> = _profileResponse

    fun getStudentProfile() =
        viewModelScope.launch {
            getStudentProfileSafeCall()
        }

    private suspend fun getStudentProfileSafeCall() {
        _profileResponse.value =
            NetworkResult.Loading(NETWORK_RESULT_MESSAGE_LOADING, NETWORK_RESULT_STATUS_LOADING)
        try {
            val token = Hawk.get<String>(Constants.PREFERENCES_TOKEN)
            val response = repository.remote.getStudentProfile(token)
            _profileResponse.value = handleStudentProfileResponse(response)
            if (profileResponse.value is NetworkResult.Success) {
                val studentProfile = profileResponse.value!!.data
                if (studentProfile != null) {
                    saveStudentProfileInRoom(studentProfile)
                }
            }

        } catch (e: Exception) {
            Log.d(
                LOG_TAG,
                "Profile View Model: getStudentProfileSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                this._profileResponse.value = NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                this._profileResponse.value =
                    NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_UNKNOWN,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun saveStudentProfileInRoom(studentProfile: StudentProfileResponse) {
        val profileEntity = ProfileEntity(studentProfile)
        insertProfile(profileEntity)
    }

    private fun handleStudentProfileResponse(response: Response<StudentProfileResponse>): NetworkResult<StudentProfileResponse> {
        Log.d(
            LOG_TAG,
            "Profile Vew Model: handleStudentProfileResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(NETWORK_RESULT_MESSAGE_TIMEOUT, response.code())
            }

            response.isSuccessful -> {
                NetworkResult.Success(response.body()!!, response.message(), response.code())
            }

            else -> {
                NetworkResult.Error(response.message(), response.code())
            }
        }
    }

}