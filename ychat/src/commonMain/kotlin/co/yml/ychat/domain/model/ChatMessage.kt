package co.yml.ychat.domain.model

/**
 * Represents a message in a conversation, consisting of a [role] indicating the speaker
 * (e.g., “system”, “user” or “assistant”), and the [content] of the message sent by the speaker.
 * @property role The role of the speaker who sends the message.
 * @property content The content of the message sent by the speaker.
 */
data class ChatMessage(
    val role: String,
    val content: List<Content>
)

sealed interface Content {
    data class Text(val text: String) : Content
    data class Image(val bytes: ByteArray, val detail: ImageDetail) : Content
    data class ImageBase64(val base64: String, val detail: ImageDetail) : Content
}

enum class ImageDetail {
    HIGH,
    LOW
}
