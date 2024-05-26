package co.yml.ychat.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChatMessageDto(
    @SerialName("role")
    val role: String,
    @SerialName("content")
    val content: List<ContentDto>,
)

@Serializable
internal data class ChatMessageResponseDto(
    @SerialName("role")
    val role: String,
    @SerialName("content")
    val content: String,
)

@Serializable
data class ContentDto(
    @SerialName("type")
    val type: String,
    @SerialName("text")
    val text: String? = null,
    @SerialName("image_url")
    val imageUrl: ImageUrlDto? = null,
)

@Serializable
data class ImageUrlDto(
    @SerialName("url")
    val url: String,
    @SerialName("detail")
    val detail: String,
)
