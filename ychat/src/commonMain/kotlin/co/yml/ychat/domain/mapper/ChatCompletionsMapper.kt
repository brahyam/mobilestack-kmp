package co.yml.ychat.domain.mapper

import co.yml.ychat.data.dto.ChatCompletionParamsDto
import co.yml.ychat.data.dto.ChatCompletionsDto
import co.yml.ychat.data.dto.ChatMessageDto
import co.yml.ychat.data.dto.ContentDto
import co.yml.ychat.data.dto.ImageUrlDto
import co.yml.ychat.domain.model.ChatCompletionsParams
import co.yml.ychat.domain.model.ChatMessage
import co.yml.ychat.domain.model.Content
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun ChatCompletionsDto.toChatMessages(): List<ChatMessage> {
    return this.choices.map {
        ChatMessage(
            it.message.role, listOf(Content.Text(it.message.content))
        )
    }
}

internal fun ChatCompletionsParams.toChatCompletionParamsDto(): ChatCompletionParamsDto {
    return ChatCompletionParamsDto(
        model = this.model,
        messages = this.messages.map {
            ChatMessageDto(it.role, it.content.map { item -> item.toContentDto() })
        },
        maxTokens = this.maxTokens,
        temperature = this.temperature,
        topP = this.topP,
        maxResults = this.maxResults,
    )
}

@OptIn(ExperimentalEncodingApi::class)
internal fun Content.toContentDto(): ContentDto {
    return when (this) {
        is Content.Text -> ContentDto(
            type = "text",
            text = this.text
        )

        is Content.Image -> ContentDto(
            type = "image_url",
            text = null,
            imageUrl = ImageUrlDto(
                url = "data:image/jpeg;base64,${Base64.encode(this.bytes)}",
                detail = this.detail.name.lowercase()
            )
        )
    }
}
