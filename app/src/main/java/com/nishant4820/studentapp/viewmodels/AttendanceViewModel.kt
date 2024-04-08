package com.nishant4820.studentapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.models.AttendanceResponse
import com.nishant4820.studentapp.utils.Constants
import com.nishant4820.studentapp.utils.Constants.ENROLLMENT_NUMBER
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ENROLLMENT
import com.nishant4820.studentapp.utils.NetworkResult
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {


    /* RETROFIT */


    private val _attendanceResponse = MutableLiveData<NetworkResult<AttendanceResponse>>()
    val attendanceResponse: LiveData<NetworkResult<AttendanceResponse>> = _attendanceResponse

    fun getStudentAttendance(queries: HashMap<String, String>) =
        viewModelScope.launch {
            getStudentAttendanceSafeCall(queries)
        }

    private suspend fun getStudentAttendanceSafeCall(queries: HashMap<String, String>) {
        _attendanceResponse.value =
            NetworkResult.Loading(NETWORK_RESULT_MESSAGE_LOADING, NETWORK_RESULT_STATUS_LOADING)
        try {
            val response = repository.remote.getStudentAttendance(queries)
            this._attendanceResponse.value = handleStudentAttendanceResponse(response)
        } catch (e: Exception) {
            Log.d(
                LOG_TAG,
                "Attendance View Model: getStudentAttendanceSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                this._attendanceResponse.value = NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                this._attendanceResponse.value =
                    NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_UNKNOWN,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun handleStudentAttendanceResponse(response: Response<AttendanceResponse>): NetworkResult<AttendanceResponse> {
        Log.d(
            LOG_TAG,
            "Attendance Vew Model: handleAttendanceResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        return when {
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error(NETWORK_RESULT_MESSAGE_TIMEOUT, response.code())
            }

            response.isSuccessful -> {
                val records = response.body()!!
                val message =
                    if (records.isEmpty()) Constants.NETWORK_RESULT_MESSAGE_NO_RESULTS else response.message()
                return NetworkResult.Success(records, message, response.code())
            }

            else -> {
                val errorBody = response.errorBody()?.string()?.let { JSONObject(it) }
                val message = errorBody?.getString("error") ?: response.message()
                NetworkResult.Error(message, response.code())
            }
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        val enrollmentNumber: Long = Hawk.get(PREFERENCES_ENROLLMENT)
        queries[ENROLLMENT_NUMBER] = String.format("%011d", enrollmentNumber)
        return queries
    }

}