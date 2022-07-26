package com.ikami.simplepay.util
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.lang.Math.pow

object Utils {
    fun getCountryCode(country: String) = when (country) {
        "Uganda" -> "+256"
        "Tanzania" -> "+255"
        "Kenya" -> "+254"
        "Nigeria" -> "+234"
        else -> null
    }
    fun convertDecimalToBinary(x: Int): String {
        return Integer.toBinaryString(x)
    }
    fun convertBinaryToDecimal(num: Long): Float {
        var num = num
        var decimalNumber = 0
        var i = 0
        var remainder: Long

        while (num.toInt() != 0) {
            remainder = num % 10
            num /= 10
            decimalNumber += (remainder * pow(2.0, i.toDouble())).toInt()
            ++i
        }
        return decimalNumber.toFloat()
    }
    fun isBinaryNumber(binaryNumber: Long): Boolean {
        var binaryNumber = binaryNumber
        while (binaryNumber > 0) {
            if (binaryNumber % 10 > 1) {
                return false
            }
            binaryNumber /= 10
        }
        return true
    }
     fun getCurrencyForCountry(country: String) = when (country) {
        "Uganda" -> "UGX"
        "Tanzania" -> "TZS"
        "Kenya" -> "KES"
        "Nigeria" -> "NGN"
        else -> null
    }
     fun checkForInternet(context: Context): Boolean {
         val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}
