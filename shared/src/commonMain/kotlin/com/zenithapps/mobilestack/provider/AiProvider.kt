package com.zenithapps.mobilestack.provider

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.chat.TextPart
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

interface AiProvider {
    suspend fun completeTextChat(prompt: String): String
    suspend fun completeTextChat(prompt: String, imageBase64: String): String
}

class MockAiProvider : AiProvider {
    override suspend fun completeTextChat(prompt: String): String {
        return "Please add your OpenAI API Key to Firebase Remote Configs as `OPENAI_API_KEY`"
    }

    override suspend fun completeTextChat(prompt: String, imageBase64: String): String {
        return "Please add your OpenAI API Key to Firebase Remote Configs as `OPENAI_API_KEY`"
    }
}

class DefaultAiProvider(apiKey: String) : AiProvider {
    private val openAi = OpenAI(token = apiKey)
    override suspend fun completeTextChat(prompt: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            n = 1,
            maxTokens = 500,
            messages = listOf(
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
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        return openAi.chatCompletion(chatCompletionRequest).choices.first().message.content ?: ""
    }

    override suspend fun completeTextChat(prompt: String, imageBase64: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            n = 1,
            maxTokens = 500,
            messages = listOf(
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
                ChatMessage(
                    role = ChatRole.User,
                    content = listOf(
                        TextPart(
                            text = prompt,
                        ),
                        ImagePart(
                            url = "data:image/jpeg;base64,${imageBase64}",
                            detail = null // null to use standard definition instead of HD
                        )
                    ),
                )
            )
        )
        return openAi.chatCompletion(chatCompletionRequest).choices.first().message.content ?: ""
    }
}