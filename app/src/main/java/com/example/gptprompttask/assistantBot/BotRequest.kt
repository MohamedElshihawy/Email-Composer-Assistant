package com.example.gptprompttask.assistantBot

import android.util.Log
import com.example.gptprompttask.assistantBot.Prompt.SecondPrompt.MODEL_RULE_DESCRIPTION
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalSerializationApi::class)
class BotRequest {

    private var httpClient: HttpClient

    private val chatHistory: JSONArray = JSONArray().apply {
        put(
            JSONObject().apply {
                put("role", "system")
                put("content", MODEL_RULE_DESCRIPTION)
            },
        )
    }

    private val modelName = "gpt-3.5-turbo"
    private val endPoint = "https://api.openai.com/v1/chat/completions"
    private val apiKey = "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"

    init {
        httpClient = HttpClient(Android) {
            install(Logging) {
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                        explicitNulls = false
                    },
                )
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                accept(ContentType.Application.Json)
            }
        }
    }

    @OptIn(InternalAPI::class)
    fun chatWithBot(message: String) = flow {
        val requestBody = JSONObject().apply {
            put("model", modelName) // Specify the GPT-3 model
            put(
                "messages",
                chatHistory.put(
                    JSONObject().apply {
                        put("role", "user")
                        put("content", message)
                    },
                ),
            )
            put("temperature", 0)
            put("max_tokens", 45)
            put("top_p", 1)
            put("frequency_penalty", 0.0)
            put("presence_penalty", 0.0)
        }.toString()

        try {
            val gptResponse = httpClient.post(endPoint) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                body = requestBody
            }

            Log.e("TAG", "chatWithBot: ${gptResponse.bodyAsText()}")
            val modelMessage = formatJsonResponse(gptResponse.bodyAsText())
            chatHistory.put(
                JSONObject().apply {
                    put("role", "assistant")
                    put("content", modelMessage)
                },
            )

            emit(
                modelMessage,
            )
        } catch (e: Exception) {
            Log.e("TAG", "chatWithBot: ${e.message}")
        }
    }

    private fun formatJsonResponse(gptResponse: String): String {
        Log.e("TAG", "formatJsonResponse: $gptResponse")
        val jsonObject = Json.parseToJsonElement(gptResponse).jsonObject
        val choicesArray: JsonArray? = jsonObject["choices"]?.jsonArray
        val firstChoice: JsonObject? = choicesArray?.firstOrNull()?.jsonObject
        val messageObject: JsonObject? = firstChoice?.get("message")?.jsonObject
        val content = messageObject?.get("content")?.toString() ?: "No response from the assistant."

        return content.split("\n").joinToString("\n")
    }
}
