package at.privatepilot.restapi.client

import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import java.util.Base64

class WebSocketClient(private val callback: WebSocketCallback) {

    private val client = OkHttpClient()
    private val wsUrl = "ws://10.0.0.245:8080" // WebSocket URL

    private var webSocket: WebSocket? = null
    private var reconnectionExecutor: ScheduledExecutorService? = null
    private val reconnectionDelay: Long = 2 // seconds

    private var token = "your_token_here"

    private lateinit var crypt : CryptoUtils

    private fun getConnection(): WebSocket {
        runBlocking {
            crypt = CryptoUtils()
            if (webSocket == null || webSocket?.send("Ping") == false) {
                webSocket = createWebSocket(crypt.encrypt(token))
            }
        }
        return webSocket as WebSocket
    }

    private fun createWebSocket(token: String): WebSocket {

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)

                webSocket.send("WebSocket connection opened")
                callback.onConnection()
                cancelReconnection()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                val decryptedMessage = crypt.decrypt(text)
                callback.onMessageReceived(decryptedMessage)
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

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("WebSocket connection failed: ${t.message}")
                callback.onConnectionFailure()

                // Start reconnection mechanism
                scheduleReconnection()
            }
        }

        val request = Request.Builder()
            .url(wsUrl)
            .addHeader("authorization", token)
            .addHeader("publickey", Base64.getEncoder().encodeToString(crypt.clientPublicKey?.encoded))
            .build()

        return client.newWebSocket(request, webSocketListener)
    }

    private fun cancelReconnection() {
        reconnectionExecutor?.shutdown()
    }

    private fun scheduleReconnection() {
        reconnectionExecutor?.shutdown()
        reconnectionExecutor = Executors.newSingleThreadScheduledExecutor()

        reconnectionExecutor?.scheduleAtFixedRate(
            { getConnection() },
            reconnectionDelay, reconnectionDelay, TimeUnit.SECONDS
        )
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
}
