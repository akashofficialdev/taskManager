package com.lens.taskmanager.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.provider.Settings.Secure.getString
import android.telephony.TelephonyManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket

object NetworkUtils {
    // check if user device is connected to internet
    suspend fun isNetworkAvailable(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
        }
    }
    suspend fun isInternetAvailable(): Boolean {
        return withContext(Dispatchers.IO){
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                true
            } catch (e: IOException) {
                false
            }

        }


    }

    // check if user device is connected to wifi
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    // check if user device is connected to mobile data
    fun isMobileDataConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    // check if user device is in Roaming
    fun isRoaming(context: Context): Boolean {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.isNetworkRoaming
    }

    // to get the device ip address
    fun getIPAddress(): String? {
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()

        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val addresses = networkInterface.inetAddresses

            while (addresses.hasMoreElements()) {
                val address = addresses.nextElement()

                if (!address.isLoopbackAddress &&
                    address is InetAddress &&
                    (address.hostAddress?.indexOf(':') ?: -1) < 0
                ) {
                    return address.hostAddress
                }
            }
        }
        return null
    }

    fun getMachineName(context: Context): String {
        val deviceId = getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        // remove spaces from name
        return "${Build.MODEL}-$deviceId".replace(" ", "")
    }
}
