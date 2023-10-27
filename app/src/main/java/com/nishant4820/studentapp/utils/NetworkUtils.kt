package com.nishant4820.studentapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.util.Log

object NetworkUtils {


    /**
     * A utility function to check if network is available or not.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return isNetworkAvailable(context, null)
    }

    /**
     * A utility function to check if network is available or not.
     * Pass [NetworkListener] object to register callbacks for network change
     * and use its [NetworkListener.networkAvailability] State Flow to collect changes.
     */
    fun isNetworkAvailable(context: Context, networkCallback: NetworkCallback?): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (networkCallback != null) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        }
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.d(Constants.LOG_TAG, "Wifi Connected")
                true
            }

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d(Constants.LOG_TAG, "Cellular Network Connected")
                true
            }

            else -> {
                Log.d(Constants.LOG_TAG, "Internet Not Connected")
                false
            }
        }
    }

}