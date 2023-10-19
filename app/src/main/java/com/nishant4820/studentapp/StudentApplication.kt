package com.nishant4820.studentapp

import android.app.Application
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_ID
import com.nishant4820.studentapp.utils.Constants.PREFERENCES_TOKEN
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StudentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Initialize Hawk
        Hawk.init(this).build()

        Hawk.put(
            PREFERENCES_TOKEN,
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NTJlZDE5NWRlZWEwYTAwNTBhMzBhNDYiLCJpYXQiOjE2OTc1NjcxODAsImV4cCI6MTcwMDE1OTE4MH0.o71g62jLDrHzUV9Ep6fU_zx-s3hGC74cQMIW26TL-Cw"
        )
        Hawk.put(PREFERENCES_ID, "652ed195deea0a0050a30a46")

    }
}