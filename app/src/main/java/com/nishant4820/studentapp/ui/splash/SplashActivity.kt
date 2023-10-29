package com.nishant4820.studentapp.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nishant4820.studentapp.ui.home.HomeActivity
import com.nishant4820.studentapp.ui.login.LoginActivity
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ENROLLMENT
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ID
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_TOKEN
import com.nishant4820.studentapp.utils.openActivity
import com.orhanobut.hawk.Hawk

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val token = Hawk.get<String>(PREFERENCES_TOKEN)
        val studentId = Hawk.get<String>(PREFERENCES_ID)
        val enrollment = Hawk.get<Long>(PREFERENCES_ENROLLMENT)
        if (token.isNullOrBlank() || studentId.isNullOrBlank() || enrollment == 0L) {
            openActivity<LoginActivity>()
        } else {
            openActivity<HomeActivity>()
        }
        finish()
    }

}