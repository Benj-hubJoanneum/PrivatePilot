package com.example.selfhostedcloudstorage.restapi.client

import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class WebApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "http://188.23.64.57:8000/storage_benjamin"

    fun requestInputStream(url: String, callback: WebApiCallback) {
        val request = Request.Builder()
            .url(baseUrl + url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    callback.onSuccess(inputStream)
                } else {
                    callback.onError(IOException("Request failed with code ${response.code}"))
                }
            }
        })
    }

    interface WebApiCallback {
        fun onSuccess(inputStream: InputStream?)
        fun onError(error: Throwable)
    }
}