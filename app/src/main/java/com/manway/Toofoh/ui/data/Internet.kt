package com.manway.Toofoh.ui.data


import Ui.enums.Availability
import okhttp3.Connection
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


fun isUrlAvailable(urlString: String): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder().url(urlString).head().build()

    return try {
        val response = client.newCall(request).execute()
        response.isSuccessful
    } catch (e: IOException) {
        false
    }
}

interface InternetListener {
    fun internet(availability: Boolean)
    fun other(e: Exception)
}