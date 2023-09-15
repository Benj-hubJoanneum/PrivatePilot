package com.example.selfhostedcloudstorage.restapi.client

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "ws://188.23.64.57:8000/storage_benjamin" // WebSocket URL

    private lateinit var webSocket: WebSocket

    fun connectWebSocket(callback: WebSocketCallback) {
        val request = Request.Builder()
            .url(baseUrl)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                // WebSocket connection has been established
                callback.onOpen()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                // Handle incoming text message
                callback.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Handle incoming binary message (if needed)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                // WebSocket is closing
                callback.onClosing(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                // WebSocket has been closed
                callback.onClosed(code, reason)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // WebSocket connection or communication has failed
                callback.onError(t)
            }
        })
    }

    fun sendWebSocketMessage(message: String) {
        webSocket.send(message)
    }

    fun closeWebSocket(code: Int, reason: String) {
        webSocket.close(code, reason)
    }

    interface WebSocketCallback {
        fun onOpen()
        fun onMessage(message: String)
        fun onClosing(code: Int, reason: String)
        fun onClosed(code: Int, reason: String)
        fun onError(error: Throwable)
    }
}