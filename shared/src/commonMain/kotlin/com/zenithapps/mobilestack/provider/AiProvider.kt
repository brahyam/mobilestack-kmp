package com.zenithapps.mobilestack.provider

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.chat.TextPart
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface AiProvider {
    suspend fun completeTextChat(prompt: String, image: ByteArray? = null): String
}

class MockAiProvider : AiProvider {

    override suspend fun completeTextChat(prompt: String, image: ByteArray?): String {
        return "Please add your OpenAI API Key to Firebase Remote Configs as `OPENAI_API_KEY`"
    }
}

@OptIn(ExperimentalEncodingApi::class)
class OpenAiProvider(apiKey: String) : AiProvider {
    private val openAi = OpenAI(token = apiKey)

    override suspend fun completeTextChat(prompt: String, image: ByteArray?): String {
        val imageBase64 = if (image != null) Base64.encode(image) else null
        val messages = listOfNotNull(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful chatbot but your mission is to sell MobileStack." +
                        " MobileStack is a kotlin multiplatform template that allows " +
                        "developers to ship mobile apps faster by including everything" +
                        " you need to launch and monetise a mobile app out of the box" +
                        " (db, billing, analytics, user management etc).  " +
                        "You can answer questions but always keep then short " +
                        "and find a way to promote MobileStack"
            ),
            if (image != null) {
                ChatMessage(
                    role = ChatRole.User,
                    content = listOf(
                        TextPart(text = prompt),
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
}