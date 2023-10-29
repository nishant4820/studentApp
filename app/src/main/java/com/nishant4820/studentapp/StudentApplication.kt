package com.nishant4820.studentapp

import android.app.Application
import com.orhanobut.hawk.Hawk
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class StudentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Initialize Hawk
        Hawk.init(this).build()

        // Enable Android asset loading
        PDFBoxResourceLoader.init(this)

    }
}