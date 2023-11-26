package at.privatepilot.restapi.client

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.IOException
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher

class WebSocketClient(private val callback: WebSocketCallback) {

    private val client = OkHttpClient()
    private val url = "ws://10.0.0.245:8080" //localhost PC
    //private val url = "ws://62.47.6.236:8080"
    //private val url = "ws://172.19.11.57:8080"
    //private val url = "ws://10.0.0.137:8080"


    private val token = "your_token_here"
    private val request = Request.Builder()
        .url(url)
        .addHeader("Authorization", token)
        .build()

    private var webSocket: WebSocket? = null
    private var reconnectionExecutor: ScheduledExecutorService? = null
    private val reconnectionDelay: Long = 2 // seconds

    fun getConnection(): WebSocket {
        if (webSocket == null || webSocket?.send("Ping") == false) {
            // If webSocket is null or sending a ping fails, recreate the connection
            webSocket = createWebSocket()
        }
        return webSocket as WebSocket
    }

    private fun createWebSocket(): WebSocket {
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)

                webSocket.send("WebSocket connection opened")
                callback.onConnection()
                cancelReconnection()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                callback.onMessageReceived(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                println("Received bytes: ${bytes.utf8()}")
                callback.onMessageReceived(bytes)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                println("WebSocket connection closed. Code: $code, Reason: $reason")
                callback.onConnectionCancel()

                // Start reconnection mechanism
                scheduleReconnection()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                println("WebSocket connection failed: ${t.message}")
                callback.onConnectionFailure()

                // Start reconnection mechanism
                scheduleReconnection()
            }
        }

        return client.newWebSocket(request, webSocketListener)
    }

    private fun cancelReconnection() {
        reconnectionExecutor?.shutdown()
    }

    private fun scheduleReconnection() {
        reconnectionExecutor?.shutdown()
        reconnectionExecutor = Executors.newSingleThreadScheduledExecutor()

        reconnectionExecutor?.scheduleAtFixedRate(
            { createWebSocket() },
            reconnectionDelay, reconnectionDelay, TimeUnit.SECONDS
        )
    }

    private fun fetchServerPublicKey(callback: PublicKeyCallback) {
        val publicKeyUrl = "http://localhost:8081/public-key" // Replace with your server's public key URL

        val request = Request.Builder()
            .url(publicKeyUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val publicKey = response.body?.string()
                    callback.onPublicKeyReceived(publicKey ?: "")
                } else {
                    callback.onPublicKeyError("Failed to fetch public key. Code: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                callback.onPublicKeyError("Error fetching public key: ${e.message}")
            }
        })
    }

    private fun encryptMessage(message: String, publicKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        val keyFactory = KeyFactory.getInstance("RSA")

        val keySpec = X509EncodedKeySpec(Base64.getDecoder().decode(publicKey))
        val serverPublicKey = keyFactory.generatePublic(keySpec)

        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey)

        val encryptedBytes = cipher.doFinal(message.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    fun sendToServer(requestMessage: String) {
        getConnection().send(requestMessage)
    }

    fun sendToServer(requestMessage: ByteString) {
        getConnection().send(requestMessage)
    }

    interface WebSocketCallback {
        fun onMessageReceived(message: String)
        fun onMessageReceived(message: ByteString)

        fun onConnection()
        fun onConnectionCancel()
        fun onConnectionFailure()
    }

    interface PublicKeyCallback {
        fun onPublicKeyReceived(publicKey: String)
        fun onPublicKeyError(error: String)
    }
}