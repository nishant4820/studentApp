plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.nishant4820.studentapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nishant4820.studentapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "v2024.04.01"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Material
    implementation("com.google.android.material:material:1.10.0")

    // Constraint Layout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation Components
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Microsoft Authentication
    implementation("com.microsoft.identity.client:msal:5.4.2")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-storage")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    // Coil
    implementation("io.coil-kt:coil:2.4.0")

    // Swipe To Refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Shared Preferences
    implementation("com.orhanobut:hawk:2.0.1")

    // PDF Helper
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    // Auto Dispose Coroutine Scope
    implementation("com.github.satoshun.coroutine.autodispose:autodispose:0.3.1")

    // Charts Library
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}

kapt {
    correctErrorTypes = true
}