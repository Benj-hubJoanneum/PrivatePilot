package at.privatepilot.restapi.client

import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import java.util.Base64
import java.security.PrivateKey

class WebSocketClient(private val callback: WebSocketCallback) {

    private val client = OkHttpClient()
    private val wsUrl = "ws://10.0.0.245:8080" // WebSocket URL
    private val publicKeyUrl = "http://10.0.0.245:8081/public-key" // Public key URL

    private var webSocket: WebSocket? = null
    private var reconnectionExecutor: ScheduledExecutorService? = null
    private val reconnectionDelay: Long = 2 // seconds

    private var token = "your_token_here"

    private val cipher: Cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    private var serverPublicKey: PublicKey? = null
    private var clientPublicKey: PublicKey? = null
    private var clientPrivateKey: PrivateKey? = null

    init {
        generateKeyPair()
    }

    fun decrypt(encryptedMessage: String): String {
        try {
            val encryptedBytes = Base64.getDecoder().decode(encryptedMessage)

            val cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")

            cipher.init(Cipher.DECRYPT_MODE, clientPrivateKey)

            val decryptedBytes = cipher.doFinal(encryptedBytes)

            return String(decryptedBytes)
        } catch (e: Exception) {
            println("Error during decryption: ${e.message}")
            // Handle the error appropriately
        }
        return ""
    }

    private fun generateKeyPair() {
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            val keyPair = keyPairGenerator.generateKeyPair()

            clientPublicKey = keyPair.public
            clientPrivateKey = keyPair.private
        } catch (e: Exception) {
            println("Error generating key pair: ${e.message}")
        }
    }
    private fun getConnection(): WebSocket {
        runBlocking {
            serverPublicKey = fetchServerPublicKey()
            if (webSocket == null || webSocket?.send("Ping") == false) {
                webSocket = createWebSocket(encrypt(token))
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
                callback.onMessageReceived(decrypt(text))
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
            .addHeader("publickey", Base64.getEncoder().encodeToString(clientPublicKey?.encoded))
            .build()

        return client.newWebSocket(request, webSocketListener)
    }

    private fun getPublicKey(publicKeyPEM: String): PublicKey {
        try {
            val keyBytes = Base64.getDecoder().decode(publicKeyPEM.trimIndent()
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", ""))
            val keySpec = X509EncodedKeySpec(keyBytes)
            val keyFactory = KeyFactory.getInstance("RSA")
            return keyFactory.generatePublic(keySpec)
        } catch (e: Exception) {
            throw RuntimeException("Error building public key: ${e.message}")
        }
    }

    private fun fetchServerPublicKey(): PublicKey? {
        try {
            val url = URL(publicKeyUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val publicKeyPEM = reader.readText()

            return getPublicKey(publicKeyPEM)
        } catch (e: Exception) {
            println("Error fetching or building public key: ${e.message}")
        }
        return null
    }

    private fun encrypt(plaintext: String) : String{
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey)

        val encryptedBytes = cipher.doFinal(plaintext.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    private fun encrypt(plaintext: ByteArray): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey)
        return cipher.doFinal(plaintext)
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
