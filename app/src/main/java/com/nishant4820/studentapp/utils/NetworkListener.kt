package com.nishant4820.studentapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import com.nishant4820.studentapp.utils.NetworkUtils.isNetworkAvailable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkListener(context: Context) : ConnectivityManager.NetworkCallback() {

    private val _networkAvailability: MutableStateFlow<Boolean>

    init {
        _networkAvailability = MutableStateFlow(isNetworkAvailable(context, this))
    }

    val networkAvailability: StateFlow<Boolean>
        get() = _networkAvailability.asStateFlow()

    override fun onAvailable(network: Network) {
        _networkAvailability.value = true
    }

    override fun onLost(network: Network) {
        _networkAvailability.value = false
    }
}