package com.example.selfhostedcloudstorage.restapi.client

import android.content.Context
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class ApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "http://91.114.196.254:8000/storage_benjamin"
    fun requestInputStream(url: String, callback: ApiCallback) {
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

    fun sendOutputStream(url: String, outputStream: OutputStream, callback: ApiCallback) {
        val request = Request.Builder()
            .url(baseUrl + url)
            .post(
                RequestBody.create(
                    null,
                    outputStream.toString()
                )
            ) // Replace null with appropriate media type
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(null) // Success, no input stream expected
                } else {
                    callback.onError(IOException("Request failed with code ${response.code}"))
                }
            }
        })
    }

    fun requestDelete(url: String, callback: ApiCallback) {
        val request = Request.Builder()
            .url(baseUrl + url)
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(null) // Success, no input stream expected
                } else {
                    callback.onError(IOException("Request failed with code ${response.code}"))
                }
            }
        })
    }

    interface ApiCallback {
        fun onSuccess(inputStream: InputStream?)
        fun onError(error: Throwable)
    }
}
