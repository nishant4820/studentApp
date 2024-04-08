package com.nishant4820.studentapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.results.ResultsEntity
import com.nishant4820.studentapp.data.models.StudentResultResponse
import com.nishant4820.studentapp.utils.Constants.ENROLLMENT_NUMBER
import com.nishant4820.studentapp.utils.Constants.FORMAT
import com.nishant4820.studentapp.utils.Constants.JSON
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    /* ROOM DATABASE */

    val readResults: LiveData<List<ResultsEntity>> = repository.local.readResults().asLiveData()

    private fun insertResults(resultsEntity: ResultsEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertResults(resultsEntity)
        }


    /* RETROFIT */


    private val _resultResponse = MutableLiveData<NetworkResult<StudentResultResponse>>()
    val resultResponse: LiveData<NetworkResult<StudentResultResponse>> = _resultResponse

    fun getStudentResult(queries: HashMap<String, String>) =
        viewModelScope.launch {
            getStudentResultSafeCall(queries)
        }

    private suspend fun getStudentResultSafeCall(queries: HashMap<String, String>) {
        _resultResponse.value =
            NetworkResult.Loading(NETWORK_RESULT_MESSAGE_LOADING, NETWORK_RESULT_STATUS_LOADING)
        try {
            val response = repository.remote.getStudentResult(queries)
            _resultResponse.value = handleStudentResultResponse(response)
            if (resultResponse.value is NetworkResult.Success) {
                val studentResult = resultResponse.value!!.data
                if (studentResult != null) {
                    offlineCacheStudentResult(studentResult)
                }
            }

        } catch (e: Exception) {
            Log.d(
                LOG_TAG,
                "Results View Model: getStudentResultSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                this._resultResponse.value = NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                this._resultResponse.value =
                    NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_UNKNOWN,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun offlineCacheStudentResult(studentResult: StudentResultResponse) {
        val resultsEntity = ResultsEntity(studentResult)
        insertResults(resultsEntity)
    }

    private fun handleStudentResultResponse(response: Response<StudentResultResponse>): NetworkResult<StudentResultResponse> {
        Log.d(
            LOG_TAG,
            "Results Vew Model: handleStudentResultResponse, response message: ${response.message()}, response code: ${response.code()}"
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

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[ENROLLMENT_NUMBER] = Hawk.get(PREFERENCES_ENROLLMENT)
        queries[FORMAT] = JSON
        return queries
    }

}