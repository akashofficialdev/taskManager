package com.android.edique.edxpert.features.base

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class ConnectivityViewModel(application: Application) : AndroidViewModel(application) {
    val isConnected: MutableLiveData<Boolean> = MutableLiveData()

    init {
        val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected.postValue(true)
            }

            override fun onLost(network: Network) {
                isConnected.postValue(false)
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }
}
