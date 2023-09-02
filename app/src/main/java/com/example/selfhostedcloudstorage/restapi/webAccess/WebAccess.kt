package com.example.selfhostedcloudstorage.restapi.webAccess

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class WebAccess private constructor() {
    companion object {
        @Volatile
        private var instance: WebAccess? = null

        @JvmStatic
        fun getInstance(): WebAccess =
            instance ?: synchronized(this) {
                instance ?: WebAccess().also { instance = it }
            }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "http://188.23.64.57:8000/storage_benjamin"

    suspend fun readNodes(path: String): List<String>? {
        val request = Request.Builder()
            .url(baseUrl + path)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val itemList = mutableListOf<String>()
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    itemList.add(responseBody)
                }
                itemList
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun createNodes(): Boolean {
        val request: Request = Request.Builder()
            .url(baseUrl)
            .build()
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    fun updateNodes(): Boolean {
        val request: Request = Request.Builder()
            .url(baseUrl)
            .build()
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
    fun deleteNodes(): Boolean {
        val request: Request = Request.Builder()
            .url(baseUrl)
            .build()
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}
