package com.nishant4820.studentapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.nishant4820.studentapp.utils.Constants.LOG_TAG
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkListener : ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable = MutableStateFlow(false)

    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(this)
        isNetworkAvailable.value = isNetworkAvailable(connectivityManager)
        return isNetworkAvailable
    }

    private fun isNetworkAvailable(connectivityManager: ConnectivityManager): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.d(LOG_TAG, "Wifi Connected")
                true
            }

            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d(LOG_TAG, "Cellular Network Connected")
                true
            }

            else -> {
                Log.d(LOG_TAG, "Internet Not Connected")
                false
            }
        }
    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}