package com.nachohseara.beermastersshop.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

class NetworkUtils {
    companion object {
        fun isOnline(ctxt: Context): Boolean {
            val connMgr = ctxt.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = connMgr.getNetworkCapabilities(connMgr.activeNetwork)
                if (capabilities != null)
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
                        else -> false
                    } else return false
            } else {
                val networkInfo = connMgr.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }

        }
    }
}