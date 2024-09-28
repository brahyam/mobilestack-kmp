package com.zenithapps.mobilestack.component

import androidx.compose.ui.graphics.ImageBitmap
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.preat.peekaboo.image.picker.toImageBitmap
import com.zenithapps.mobilestack.component.SampleAiHomeComponent.Model
import com.zenithapps.mobilestack.component.SampleAiHomeComponent.Output
import com.zenithapps.mobilestack.provider.AiProvider
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.InAppNotificationProvider
import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification
import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification.Duration
import com.zenithapps.mobilestack.provider.OSCapabilityProvider
import com.zenithapps.mobilestack.useCase.SignUpUseCase
import com.zenithapps.mobilestack.util.createCoroutineScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface SampleAiHomeComponent {
    val model: Value<Model>

    data class Model(
        // TIP: things you want to be dynamic or your users to interact with
        val loading: Boolean = true,
        val prompt: String = "",
        val image: ImageBitmap? = null,
        val capturing: Boolean = false,
        val messages: List<String> = emptyList()
    )

    // TIP: a function for each user interaction
    fun onPromptChanged(prompt: String)
    fun onProfileTap()
    fun onSubmitTap()
    fun onCameraTap()
    fun onImageSelected(byteArray: ByteArray)
    fun onCloseCameraTap()
    fun onRequestCameraPermissionTap()
    fun onRemoveImageTap()

    sealed interface Output {
        // TIP: an object for each navigation action
        data object GoToProfile : Output
    }
}

@OptIn(ExperimentalEncodingApi::class)
class DefaultSampleAiHomeComponent(
    componentContext: ComponentContext,
    private val authProvider: AuthProvider,
    private val signUp: SignUpUseCase,
    private val inAppNotificationProvider: InAppNotificationProvider,
    private val aiProvider: AiProvider,
    private val osCapabilityProvider: OSCapabilityProvider,
    private val onOutput: (Output) -> Unit
) : SampleAiHomeComponent, ComponentContext by componentContext {
    private val scope = createCoroutineScope()
    private var imageBase64: String? = null // Prepares image to be sent to the AiProvider

    override val model = MutableValue(Model())


    init {
        scope.launch {
            if (!authProvider.isLoggedIn()) {
                try {
                    signUp.anonymously()

                } catch (e: Exception) {
                    Napier.e(e) { "Error signing up anonymously" }
                    inAppNotificationProvider.showNotification(
                        Notification(
                            message = e.message ?: "Unknown error",
                            duration = Duration.LONG
                        )
                    )
                }
            }
            model.value = model.value.copy(loading = false)
        }
    }

    override fun onPromptChanged(prompt: String) {
        model.value = model.value.copy(prompt = prompt)
    }

    override fun onProfileTap() {
        onOutput(Output.GoToProfile)
    }

    override fun onSubmitTap() {
        model.value = model.value.copy(loading = true)
        scope.launch {
            try {
                val result = if (imageBase64 != null) {
                    aiProvider.completeTextChat(model.value.prompt.trim(), imageBase64!!)
                } else {
                    aiProvider.completeTextChat(model.value.prompt.trim())
                }
                model.value = model.value.copy(
                    prompt = "",
                    image = null,
                    messages = model.value.messages + model.value.prompt.trim() + result
                )
                imageBase64 = null
            } catch (e: Exception) {
                Napier.e(e) { "Error calling completions API" }
                inAppNotificationProvider.showNotification(
                    Notification(
                        message = e.message ?: "Unknown error",
                        duration = Duration.LONG
                    )
                )
            } finally {
                model.value = model.value.copy(loading = false)
            }
        }
    }

    override fun onCameraTap() {
        model.value = model.value.copy(capturing = true)
    }

    override fun onImageSelected(byteArray: ByteArray) {
        model.value =
            model.value.copy(image = byteArray.toImageBitmap(), capturing = false, loading = true)
        scope.launch {
            imageBase64 = Base64.encode(byteArray)
            model.value = model.value.copy(loading = false)
        }
    }

    override fun onCloseCameraTap() {
        model.value = model.value.copy(capturing = false)
    }

    override fun onRequestCameraPermissionTap() {
        osCapabilityProvider.openAppSettings()
    }

    override fun onRemoveImageTap() {
        model.value = model.value.copy(image = null)
        imageBase64 = null
    }
}