package com.example.selfhostedcloudstorage.restapi.client

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketClient(private val callback: WebSocketCallback) {

    private val client = OkHttpClient()
    private val url = "ws://10.0.0.245:8080"
    private val request = Request
        .Builder()
        .url(url)
        .build()

    private var webSocket: WebSocket? = null

    fun getConnection(): WebSocket {

        if (webSocket == null) { // TODO: redo connection if down

            val webSocketListener = object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                    super.onOpen(webSocket, response)
                    webSocket.send("WebSocket connection opened")
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
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: okhttp3.Response?
                ) {
                    super.onFailure(webSocket, t, response)
                    println("WebSocket connection failed: ${t.message}")
                }
            }
            webSocket = client.newWebSocket(request, webSocketListener)
        }
        return webSocket as WebSocket
    }

    interface WebSocketCallback {
        fun onMessageReceived(message: String)
        fun onMessageReceived(message: ByteString)
    }

}
