package at.privatepilot.restapi.client

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class WebSocketClient(private val callback: WebSocketCallback) {

    private val client = OkHttpClient()
    private val url = "ws://10.0.0.245:8080" //localhost PC
    //private val url = "ws://62.47.6.236:8080"
    //private val url = "ws://172.19.11.57:8080"
    //private val url = "ws://10.0.0.137:8080"
    private val request = Request.Builder()
        .url(url)
        .build()

    private var webSocket: WebSocket? = null
    private var reconnectionExecutor: ScheduledExecutorService? = null
    private val reconnectionDelay: Long = 2 // seconds

    fun getConnection(): WebSocket {
        if (webSocket == null || webSocket?.send("Ping") == false) {
            // If webSocket is null or sending a ping fails, recreate the connection
            createWebSocket()
        }
        return webSocket as WebSocket
    }

    private fun createWebSocket() {
        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                webSocket.send("WebSocket connection opened")
                callback.onConnection()

                // Cancel reconnection attempts if the connection is successfully opened
                cancelReconnection()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                println("Received message: $text")
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

        webSocket = client.newWebSocket(request, webSocketListener)
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

    interface WebSocketCallback {
        fun onMessageReceived(message: String)
        fun onMessageReceived(message: ByteString)

        fun onConnection()
        fun onConnectionCancel()
        fun onConnectionFailure()
    }
}