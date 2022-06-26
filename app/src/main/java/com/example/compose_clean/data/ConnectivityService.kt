package com.example.compose_clean.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ConnectivityService {

    fun hasInternetConnection(): Boolean

    fun hasNoInternetConnection(): Boolean

}

class ConnectivityServiceLogic @Inject constructor(@ApplicationContext context: Context) : ConnectivityService {

    private var connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    override fun hasInternetConnection(): Boolean {
        var hasNetworkConnectivity = false
        connectivityManager?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getNetworkCapabilities(activeNetwork)?.run {
                    hasNetworkConnectivity = hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                }
            } else {
                activeNetworkInfo?.type.let {
                    hasNetworkConnectivity = it == ConnectivityManager.TYPE_WIFI ||
                            it == ConnectivityManager.TYPE_MOBILE ||
                            it == ConnectivityManager.TYPE_VPN
                }
            }
        }
        return hasNetworkConnectivity
    }

    override fun hasNoInternetConnection(): Boolean = !hasInternetConnection()

}