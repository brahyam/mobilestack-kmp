package com.getmobilestack.kmp.provider

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.chat.TextPart
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface AiProvider {
    suspend fun complete(
        systemPrompt: String,
        userPrompt: String,
        userImage: ByteArray? = null,
    ): String

    suspend fun <T> complete(
        systemPrompt: String,
        userPrompt: String,
        userImage: ByteArray?,
        serializer: DeserializationStrategy<T>,
    ): T
}

class MockAiProvider : AiProvider {

    override suspend fun complete(
        systemPrompt: String,
        userPrompt: String,
        userImage: ByteArray?,
    ): String {
        return "Please add your OpenAI API Key to Firebase Remote Configs as `OPENAI_API_KEY`"
    }

    override suspend fun <T> complete(
        systemPrompt: String,
        userPrompt: String,
        userImage: ByteArray?,
        serializer: DeserializationStrategy<T>,
    ): T {
        throw IllegalStateException("Please add your OpenAI API Key to Firebase Remote Configs as `OPENAI_API_KEY`")
    }
}

@OptIn(ExperimentalEncodingApi::class)
class OpenAiProvider(apiKey: String) : AiProvider {
    private val openAi = OpenAI(token = apiKey)

    override suspend fun complete(
        systemPrompt: String,
        userPrompt: String,
        userImage: ByteArray?,
    ): String {
        val imageBase64 = if (userImage != null) Base64.encode(userImage) else null
        val messages = listOfNotNull(
            ChatMessage(
                role = ChatRole.System,
                content = systemPrompt
            ),
            if (userImage != null) {
                ChatMessage(
                    role = ChatRole.User,
                    content = listOf(
                        TextPart(text = userPrompt),
                        ImagePart(url = "data:image/jpeg;base64,${imageBase64}", detail = null)
                    ),
                )
            } else {
                null
            }
        )
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            n = 1,
            maxTokens = 500,
            messages = messages
        )
        return openAi.chatCompletion(chatCompletionRequest).choices.first().message.content ?: ""
    }

    override suspend fun <T> complete(
        systemPrompt: String,
        userPrompt: String,
        userImage: ByteArray?,
        serializer: DeserializationStrategy<T>,
    ): T {
        val imageBase64 = if (userImage != null) Base64.encode(userImage) else null
        val result = openAi.chatCompletion(
            ChatCompletionRequest(
                model = ModelId("gpt-4o-mini"),
                n = 1,
                responseFormat = ChatResponseFormat.JsonObject,
                messages = listOf(
                    ChatMessage(role = ChatRole.System, content = systemPrompt),
                    if (userImage != null) {
                        ChatMessage(
                            role = ChatRole.User,
                            content = listOf(
                                TextPart(text = userPrompt),
                                ImagePart(
                                    url = "data:image/jpeg;base64,${imageBase64}",
                                    detail = null
                                )
                            ),
                        )
                    } else {
                        ChatMessage(role = ChatRole.User, content = userPrompt)
                    }

                )
            )
        ).choices.first().message.content ?: ""
        return Json.decodeFromString(serializer, result)
    }
}