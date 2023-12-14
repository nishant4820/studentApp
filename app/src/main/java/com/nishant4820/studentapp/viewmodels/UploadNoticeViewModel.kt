package com.nishant4820.studentapp.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.MyTypeConverter
import com.nishant4820.studentapp.data.models.NoticeData
import com.nishant4820.studentapp.data.models.NoticeFormState
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
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UploadNoticeViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private val _noticeForm = MutableLiveData<NoticeFormState>()
    val noticeFormState: LiveData<NoticeFormState> = _noticeForm

    private val _postNoticeResponse = MutableLiveData<NetworkResult<NoticeData>>()
    val postNoticeResponse: LiveData<NetworkResult<NoticeData>> = _postNoticeResponse

    fun postNotice(noticeRequestBody: NoticeData) {
        viewModelScope.launch {
            postNoticeSafeCall(noticeRequestBody)
        }
    }

    private suspend fun postNoticeSafeCall(noticeRequestBody: NoticeData) {
        _postNoticeResponse.value = NetworkResult.Loading(
            NETWORK_RESULT_MESSAGE_LOADING,
            NETWORK_RESULT_STATUS_LOADING
        )
        try {
            val token = Hawk.get<String>(Constants.PREFERENCES_TOKEN)
            val response = repository.remote.postNotice(token, noticeRequestBody)
            _postNoticeResponse.value = handlePostNoticeResponse(response)
            if (_postNoticeResponse.value is NetworkResult.Success) {
                val postNoticeResult = _postNoticeResponse.value!!.data
                if (postNoticeResult != null) {
//                    TODO: Cache Response
                }
            }
        } catch (e: Exception) {
            Log.d(
                LOG_TAG,
                "Upload Notice Vew Model: postNoticeSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                _postNoticeResponse.value = NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                _postNoticeResponse.value =
                    NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_UNKNOWN,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun handlePostNoticeResponse(response: Response<NoticeData>): NetworkResult<NoticeData> {
        Log.d(
            LOG_TAG,
            "Upload Notice Vew Model: handlePostNoticeResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    response.code()
                )
            }

            response.isSuccessful -> {
                val noticeData = response.body()
                return NetworkResult.Success(noticeData!!, response.message(), response.code())
            }

            else -> {
                val errorResponse = response.errorBody()?.string()
                    ?.let { MyTypeConverter.stringToNoticeData(it) }
                val message = errorResponse?.message ?: response.message()
                return NetworkResult.Error(message, response.code())
            }
        }
    }

    fun noticeFormDataChanged(enrollment: String, password: String) {
        if (!isInputFieldValid(enrollment)) {
            _noticeForm.value = NoticeFormState(enrollmentError = R.string.invalid_username)
        } else if (!isInputFieldValid(password)) {
            _noticeForm.value = NoticeFormState(passwordError = R.string.invalid_password)
        } else {
            _noticeForm.value = NoticeFormState(isDataValid = true)
        }
    }


    // A placeholder input field validation check
    private fun isInputFieldValid(value: String): Boolean {
        return value.length >= 5
    }

}