package com.nishant4820.studentapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.nishant4820.studentapp.data.models.LoginRequestBody
import com.nishant4820.studentapp.databinding.ActivityLoginBinding
import com.nishant4820.studentapp.ui.home.HomeActivity
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_NO_INTERNET
import com.nishant4820.studentapp.utils.Constants.NETWORK_RESULT_MESSAGE_UNKNOWN
import com.nishant4820.studentapp.utils.NetworkResult
import com.nishant4820.studentapp.utils.NetworkUtils
import com.nishant4820.studentapp.utils.afterTextChanged
import com.nishant4820.studentapp.utils.openActivity
import com.nishant4820.studentapp.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var enrollmentNo: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enrollmentNo = binding.enrollmentEditText
        password = binding.passwordEditText
        loginButton = binding.btnLogin
        progressBar = binding.progressBar

        loginViewModel.loginFormState.observe(this) { loginState ->

            // disable login button unless both username / password is valid
            loginButton.isEnabled = loginState.isDataValid

            if (loginState.enrollmentError != null) {
                enrollmentNo.error = getString(loginState.enrollmentError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        }


        enrollmentNo.afterTextChanged {
            loginViewModel.loginDataChanged(
                enrollmentNo.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    enrollmentNo.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        Log.d(LOG_TAG, "button activated? ${binding.btnLogin.isEnabled}")
                        if (binding.btnLogin.isEnabled) {
                            login()
                        }
                    }
                }
                false
            }
        }

        loginButton.setOnClickListener {
            login()
        }

        loginViewModel.loginResponse.observe(this) { response ->

            Log.d(
                LOG_TAG,
                "Login Activity: Login response observer, response code: ${response.statusCode}"
            )
            when (response) {
                is NetworkResult.Success -> {
                    progressBar.visibility = View.GONE
                    Log.d(
                        LOG_TAG,
                        "Login Activity: Login response observer, response state: success"
                    )
                    openActivity<HomeActivity>()
                    finish()
                }

                is NetworkResult.Error -> {
                    progressBar.visibility = View.GONE
                    val message = response.message ?: NETWORK_RESULT_MESSAGE_UNKNOWN
                    Log.d(
                        LOG_TAG,
                        "Login Activity: Login response observer, response state: error, message: $message"
                    )
                    Snackbar.make(binding.btnLogin, message, Snackbar.LENGTH_SHORT)
                        .setAction("OK", null).show()
                }

                is NetworkResult.Loading -> {
                    Log.d(
                        LOG_TAG,
                        "Login Activity: Login response observer, response state: loading"
                    )
                }
            }

        }

    }

    private fun login() {
        Log.d(LOG_TAG, "Login Activity: login()")
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(
                binding.btnLogin,
                NETWORK_RESULT_MESSAGE_NO_INTERNET,
                Snackbar.LENGTH_SHORT
            ).setAction("Settings") {
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }.show()
            return
        }
        progressBar.visibility = View.VISIBLE
        loginViewModel.login(
            LoginRequestBody(
                enrollmentNo.text.toString().toLong(),
                password.text.toString()
            )
        )
    }

}

