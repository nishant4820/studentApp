package com.nishant4820.studentapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.MyTypeConverter
import com.nishant4820.studentapp.data.models.LoginFormState
import com.nishant4820.studentapp.data.models.LoginRequestBody
import com.nishant4820.studentapp.data.models.LoginResponse
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_LOADING
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_TIMEOUT
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_STATUS_UNKNOWN
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ENROLLMENT
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ID
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_TOKEN
import com.nishant4820.studentapp.utils.NetworkResult
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: Repository) :
    ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResponse = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponse: LiveData<NetworkResult<LoginResponse>> = _loginResponse

    fun login(loginRequestBody: LoginRequestBody) {
        viewModelScope.launch {
            loginSafeCall(loginRequestBody)
        }
    }

    private suspend fun loginSafeCall(loginRequestBody: LoginRequestBody) {
        _loginResponse.value = NetworkResult.Loading(
            NETWORK_RESULT_MESSAGE_LOADING,
            NETWORK_RESULT_STATUS_LOADING
        )
        try {
            val response = repository.remote.login(loginRequestBody)
            _loginResponse.value = handleLoginResponse(response)
            if (_loginResponse.value is NetworkResult.Success) {
                val loginResult = _loginResponse.value!!.data
                if (loginResult != null) {
                    Hawk.put(PREFERENCES_TOKEN, "Bearer ${loginResult.token}")
                    Hawk.put(PREFERENCES_ID, loginResult.id)
                    Hawk.put(PREFERENCES_ENROLLMENT, loginResult.enrollment)
                }
            }
        } catch (e: Exception) {
            Log.d(
                LOG_TAG,
                "Login Vew Model: loginSafeCall, exception message: ${e.message}"
            )
            if (e.message.toString().contains("timeout")) {
                _loginResponse.value = NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    NETWORK_RESULT_STATUS_TIMEOUT
                )
            } else {
                _loginResponse.value =
                    NetworkResult.Error(
                        NETWORK_RESULT_MESSAGE_UNKNOWN,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    private fun handleLoginResponse(response: Response<LoginResponse>): NetworkResult<LoginResponse> {
        Log.d(
            LOG_TAG,
            "Login Vew Model: handleLoginResponse, response message: ${response.message()}, response code: ${response.code()}"
        )
        when {
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error(
                    NETWORK_RESULT_MESSAGE_TIMEOUT,
                    response.code()
                )
            }

            response.isSuccessful -> {
                val loginResult = response.body()
                return NetworkResult.Success(loginResult!!, response.message(), response.code())
            }

            else -> {
                val loginErrorResponse = response.errorBody()?.string()
                    ?.let { MyTypeConverter.stringToLoginResponse(it) }
                val message = loginErrorResponse?.message ?: response.message()
                return NetworkResult.Error(message, response.code())
            }
        }
    }

    fun loginDataChanged(enrollment: String, password: String) {
        if (!isEnrollmentNoValid(enrollment)) {
            _loginForm.value = LoginFormState(enrollmentError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }


    // A placeholder username validation check
    private fun isEnrollmentNoValid(username: String): Boolean {
        return username.length == 11
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}