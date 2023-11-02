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

    private val baseUrl = "http://62.47.7.239:8000/storage_benjamin"
    private val baseRequest = { url: String -> Request.Builder().url(baseUrl + url) }

    private fun connection(request: Request, callback: ApiCallback){
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
    fun requestInputStream(url: String, callback: ApiCallback) {
        val request = baseRequest(url).build()

        connection(request, callback)
    }

    fun requestPostFile(url: String, file: File, callback: ApiCallback) {
        val request = baseRequest(url)
            .post(MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, RequestBody.create("application/octet-stream".toMediaTypeOrNull(), file))
                .build())
            .build()

        connection(request, callback)
    }

    fun requestDelete(url: String, callback: ApiCallback) {
        val request = baseRequest(url)
            .delete()
            .build()

        connection(request, callback)
    }

    interface ApiCallback {
        fun onSuccess(inputStream: InputStream?)
        fun onError(error: Throwable)
    }
}
