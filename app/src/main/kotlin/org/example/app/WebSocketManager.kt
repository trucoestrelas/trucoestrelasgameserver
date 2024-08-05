class WebSocketManager {
    private val client = OkHttpClient()
    private val connections = mutableMapOf<String, WebSocket>()
    private val messageChannels = mutableMapOf<String, Channel<Message>>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private fun createListener(userId: String): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("WebSocket opened for user $userId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received message from user $userId: $text")
                val message = Json.decodeFromString<Message>(text)
                scope.launch {
                    messageChannels[userId]?.send(message)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("Closing: $code / $reason for user $userId")
                webSocket.close(NORMAL_CLOSURE_STATUS, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Error for user $userId: " + t.message)
            }
        }
    }

    fun connect(userId: String, url: String) {
        val request = Request.Builder().url(url).build()
        val webSocket = client.newWebSocket(request, createListener(userId))
        connections[userId] = webSocket
        messageChannels[userId] = Channel()
    }

    fun disconnect(userId: String) {
        connections[userId]?.close(NORMAL_CLOSURE_STATUS, "Client closed")
        connections.remove(userId)
        messageChannels[userId]?.close()
        messageChannels.remove(userId)
    }

    suspend fun sendMessage(userId: String, message: Message) {
        val jsonMessage = Json.encodeToString(message)
        connections[userId]?.send(jsonMessage)
    }

    fun startListening(userId: String, timeoutMillis: Long, onMessage: (Message) -> Unit) {
        scope.launch {
            withTimeoutOrNull(timeoutMillis) {
                for (message in messageChannels[userId]!!) {
                    onMessage(message)
                }
            }
        }
    }

    fun stopListening(userId: String) {
        messageChannels[userId]?.close()
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}
