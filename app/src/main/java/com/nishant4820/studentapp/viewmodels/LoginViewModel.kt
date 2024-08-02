package com.nishant4820.studentapp.viewmodels

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException
import com.nishant4820.studentapp.R
import com.nishant4820.studentapp.data.Repository
import com.nishant4820.studentapp.data.database.MyTypeConverter
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    private val _loginResponse = MutableLiveData<NetworkResult<LoginResponse>>()
    val loginResponse: LiveData<NetworkResult<LoginResponse>> = _loginResponse

    private lateinit var msalApp: ISingleAccountPublicClientApplication

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                msalApp = PublicClientApplication.createSingleAccountPublicClientApplication(
                    getApplication(),
                    R.raw.auth_config_single_account
                )
            } catch (e: MsalException) {
                _loginResponse.value =
                    NetworkResult.Error(
                        e.message,
                        NETWORK_RESULT_STATUS_UNKNOWN
                    )
            }
        }
    }

    fun signInWithMSAL(activity: Activity) {
        val parameters = SignInParameters.builder()
            .withActivity(activity)
            .withLoginHint(null)
            .withScopes(listOf("User.Read"))
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    viewModelScope.launch {
//                        TODO("Modify below code when backend login is ready")
//                        loginSafeCall(LoginRequestBody(authenticationResult.accessToken))
                        _loginResponse.value =
                            NetworkResult.Error(
                                "Access Token: ${authenticationResult.accessToken}",
                                200
                            )
                    }
                }

                override fun onError(exception: MsalException) {
                    _loginResponse.value =
                        NetworkResult.Error(
                            exception.message,
                            NETWORK_RESULT_STATUS_UNKNOWN
                        )
                }

                override fun onCancel() {
                    _loginResponse.value =
                        NetworkResult.Error(
                            "Sign in cancelled",
                            NETWORK_RESULT_STATUS_UNKNOWN
                        )
                }
            })
            .build()

        msalApp.signIn(parameters)
    }

    private suspend fun loginSafeCall(loginRequestBody: LoginRequestBody) {
        _loginResponse.value = NetworkResult.Loading(
            NETWORK_RESULT_MESSAGE_LOADING,
            NETWORK_RESULT_STATUS_LOADING
        )
        try {
            val response = repository.remote.login(loginRequestBody)
            _loginResponse.value = handleLoginResponse(response)
            if (loginResponse.value is NetworkResult.Success) {
                val loginResult = loginResponse.value!!.data
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

}