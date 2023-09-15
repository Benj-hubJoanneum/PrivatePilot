package com.example.selfhostedcloudstorage.restapi.controller

import android.content.Context
import android.os.Environment
import com.example.selfhostedcloudstorage.model.INode
import com.example.selfhostedcloudstorage.model.directoryItem.DirectoryItem
import com.example.selfhostedcloudstorage.model.nodeItem.NodeItem
import com.example.selfhostedcloudstorage.restapi.client.ApiClient
import com.example.selfhostedcloudstorage.restapi.model.MetadataResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader

class ControllerNode {

    var directoryList = mutableSetOf<DirectoryItem>()
    var _nodeList = mutableSetOf<INode>()

    private var listener: ControllerListener? = null

    suspend fun createNodes(url: String, nodeData: NodeItem) {
        val apiClient = ApiClient()

        // Convert your nodeData object to JSON, assuming you have a function for that
        val jsonNodeData = convertNodeDataToJson(nodeData)

        // Convert the JSON string to bytes
        val jsonBytes = jsonNodeData.toByteArray(Charsets.UTF_8)

        // Create an OutputStream from the byte array
        val outputStream = ByteArrayOutputStream()
        outputStream.write(jsonBytes)

        apiClient.sendOutputStream(url, outputStream, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                // Handle success, if needed (POST request may or may not return a response body)
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
    }

    suspend fun readNodes(url: String) {
        val apiClient = ApiClient()

        withContext(Dispatchers.IO) {
            apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
                override fun onSuccess(inputStream: InputStream?) {
                    try {
                        val json = convertInputStreamToString(inputStream)
                        val data = json.parseItemsFromResponse()

                        directoryList.addAll(data.items.filter { it.type == "folder" }.map {
                            DirectoryItem(it.name, "$url/${it.name}")
                        })
                        _nodeList =
                            data.items.map { NodeItem(it.name, "$url/${it.name}") }.toMutableSet()
                        listener?.onSourceChanged()
                    } catch (e: IOException) {
                        println("Error parsing JSON: ${e.message}")
                    }
                }

                override fun onError(error: Throwable) {
                    println("Error: ${error.message}")
                }
            })
        }
    }
    suspend fun updateNodes(url: String) {

    }
    suspend fun deleteNodes(url: String) {
        val apiClient = ApiClient()

        apiClient.requestDelete(url, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                // Handle success, if needed (DELETE request usually doesn't return a response body)
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
    }

    suspend fun downloadFile(url: String) {
        val apiClient = ApiClient()

        val destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    if (inputStream != null) {
                        val fileName = "received_file.ext"
                        val outputFile = File(destinationDir, fileName)

                        // Create an output stream to write the input stream to the file
                        val fileOutputStream = FileOutputStream(outputFile)
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream.close()
                        inputStream.close()
                    }
                } catch (e: IOException) {
                    println("Error saving file: ${e.message}")
                }
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
    }

    private fun convertNodeDataToJson(node: NodeItem): String {
        val gson = Gson()
        return gson.toJson(node)
    }


    private fun convertInputStreamToString(inputStream: InputStream?): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        return stringBuilder.toString()
    }
    private fun String.parseItemsFromResponse(): MetadataResponse {
        return try {
            Gson().fromJson(this, MetadataResponse::class.java)
        } catch (e: Exception) {
            MetadataResponse(listOf())
        }
    }
    fun addListener(controllerListener: ControllerListener) {
        listener = controllerListener
    }
}
