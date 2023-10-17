package com.example.selfhostedcloudstorage.restapi.client

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class ApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "http://91.114.199.59:8000/storage_benjamin"
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

    fun sendOutputStream(url: String, file: File, callback: ApiCallback) {
        val request = Request.Builder()
            .url(baseUrl + url)
            .post(MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file))
                .build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    callback.onSuccess(null)
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
