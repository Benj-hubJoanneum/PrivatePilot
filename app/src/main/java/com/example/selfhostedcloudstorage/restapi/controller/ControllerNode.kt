package com.example.selfhostedcloudstorage.restapi.controller

import android.content.Context
import android.content.ContextWrapper
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
        withContext(Dispatchers.IO) {
            outputStream.write(jsonBytes)
        }

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
    fun deleteNodes(url: String) {
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
    fun downloadFile(url: String, context: Context) {
        val apiClient = ApiClient()

        val destinationDir = File(Environment.getExternalStorageDirectory(), "helloWorld")
        //Note that alternatives such as Context.getExternalFilesDir(String) or MediaStore offer better performance.
        destinationDir.mkdirs() //create folder with parents

        apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    if (inputStream != null) {

                        val fileName = url.substringAfter('/')

                        //public data
                        var file = context.getExternalFilesDir("public_HelloWorld")
                        var outputFile = File(file, fileName)

                        var fileOutputStream = FileOutputStream(outputFile)
                        var buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }

                        //private data
                        val wrapper = ContextWrapper(context)
                        file = wrapper.getDir("private_HelloWorld", Context.MODE_PRIVATE )
                        outputFile = File(file, fileName)

                        fileOutputStream = FileOutputStream(outputFile)
                        buffer = ByteArray(1024)

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream.close()
                        inputStream.close()

                        if (outputFile.exists()) {

                            var x = "yeehaaa"
                        }
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
/*    fun downloadFile(url: String, context: Context) {
        val apiClient = ApiClient()

        val destinationDir = File(Environment.getExternalStorageDirectory(), "helloWorld")
        //Note that alternatives such as Context.getExternalFilesDir(String) or MediaStore offer better performance.
        destinationDir.mkdirs() //create folder with parents

        apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    if (inputStream != null) {
                        val x = context.getExternalFilesDir("helloWorld")
                        val wrapper = ContextWrapper(context)
                        var file = wrapper.getDir("helloWorld", Context.MODE_PRIVATE )
                        val fileName = url.substringAfter('/')
                        val outputFile = File(file, fileName)

                        val fileOutputStream = FileOutputStream(outputFile)
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream.close()
                        inputStream.close()

                        if (outputFile.exists()) {

                            var x = "yeehaaa"
                        }
                    }
                } catch (e: IOException) {
                    println("Error saving file: ${e.message}")
                }
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
    }*/

  /*  suspend fun downloadFile(url: String, context: Context) {
        val apiClient = ApiClient()

        val destinationDir = File(Environment.getDataDirectory(), "selfhostedcloudstorage")

        destinationDir.mkdirs() // Create the folder with parents if it doesn't exist

        apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    if (inputStream != null) {
                        val fileName = url.substringAfter('/')
                        val outputFile = File(destinationDir, fileName)

                        val fileOutputStream = FileOutputStream(outputFile)
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream.close()
                        inputStream.close()

                        // Now, if you want to make this file visible in the system's MediaStore, you can use the following code:

                        // Create a ContentValues object to insert the file into MediaStore
                        val contentValues = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(MediaStore.Images.Media.MIME_TYPE, "application/octet-stream")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                        }

                        // Insert the file into MediaStore
                        val contentResolver = context.contentResolver
                        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                        // Write the downloaded file into the MediaStore content URI
                        val outputStream = uri?.let { contentResolver.openOutputStream(it) }
                        outputStream?.use { output ->
                            FileInputStream(outputFile).use { input ->
                                input.copyTo(output)
                            }
                        }

                        // Finally, you may want to notify the MediaScanner to scan the new file
                        context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                    }
                } catch (e: IOException) {
                    println("Error saving file: ${e.message}")
                }
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
    }*/
/*    suspend fun downloadFile(url: String) {
        val apiClient = ApiClient()

        val destinationDir = File(Environment.getExternalStorageDirectory(), "storage_benjamin")

        destinationDir.mkdirs() //create folder with parents

        apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    if (inputStream != null) {
                        val fileName = url.substringAfter('/')
                        val outputFile = File(destinationDir, fileName)

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
    }*/

    /*suspend fun downloadFile(url: String) {
        val apiClient = ApiClient()

        // Define the custom folder name
        val customFolderName = "MyCustomFolder" // Replace with your desired folder name

        // Get the external storage directory
        val externalStorageDirectory = File(Environment.getExternalStorageDirectory(), customFolderName)

        // Ensure that the custom folder and its parent directories exist
        externalStorageDirectory.mkdirs()

        apiClient.requestInputStream(url, object : ApiClient.ApiCallback {
            override fun onSuccess(inputStream: InputStream?) {
                try {
                    if (inputStream != null) {
                        val fileName = url.substringAfter('/')
                        val outputFile = File(externalStorageDirectory, fileName)

                        // Create an output stream to write the input stream to the file
                        val fileOutputStream = FileOutputStream(outputFile)
                        val buffer = ByteArray(1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead)
                        }
                        fileOutputStream.close()
                        inputStream.close()
                        println("File saved to ${outputFile.absolutePath}")
                    }
                } catch (e: IOException) {
                    println("Error saving file: ${e.message}")
                }
            }

            override fun onError(error: Throwable) {
                println("Error: ${error.message}")
            }
        })
    }*/

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
