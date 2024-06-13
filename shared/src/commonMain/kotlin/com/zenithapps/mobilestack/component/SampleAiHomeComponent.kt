package com.zenithapps.mobilestack.component

import co.yml.ychat.YChat
import co.yml.ychat.domain.model.Content
import co.yml.ychat.entrypoint.features.ChatCompletions
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.SampleAiHomeComponent.Model
import com.zenithapps.mobilestack.component.SampleAiHomeComponent.Output
import com.zenithapps.mobilestack.provider.AuthProvider
import com.zenithapps.mobilestack.provider.NotificationProvider
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification
import com.zenithapps.mobilestack.provider.NotificationProvider.Notification.Duration
import com.zenithapps.mobilestack.provider.RemoteConfigProvider
import com.zenithapps.mobilestack.useCase.SignUpUseCase
import com.zenithapps.mobilestack.util.createCoroutineScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

interface SampleAiHomeComponent {
    val model: Value<Model>

    data class Model(
        // TIP: things you want to be dynamic or your users to interact with
        val loading: Boolean = true,
        val prompt: String = "",
        val messages: List<String> = emptyList(),
    )

    // TIP: a function for each user interaction
    fun onPromptChanged(prompt: String)
    fun onProfileTap()
    fun onSubmitTap()

    sealed interface Output {
        // TIP: an object for each navigation action
        data object GoToProfile : Output
    }
}

class DefaultSampleAiHomeComponent(
    componentContext: ComponentContext,
    private val authProvider: AuthProvider,
    private val signUp: SignUpUseCase,
    private val remoteConfigProvider: RemoteConfigProvider,
    private val notificationProvider: NotificationProvider,
    private val onOutput: (Output) -> Unit
) : SampleAiHomeComponent, ComponentContext by componentContext {
    private val scope = createCoroutineScope()
    private lateinit var chatCompletion: ChatCompletions

    override val model = MutableValue(Model())

    init {
        scope.launch {// TIP: wrap network or long running operations in launch
            if (!authProvider.isLoggedIn()) {
                try {
                    signUp.anonymously()

                } catch (e: Exception) {
                    Napier.e(e) { "Error signing up anonymously" }
                    notificationProvider.showNotification(
                        Notification(
                            message = e.message ?: "Unknown error",
                            duration = Duration.LONG
                        )
                    )
                }
            }
            try {
                // TODO: Create an OpenAI API Key and add it to Firebase remote configs with key OPENAI_API_KEY
                val apiKey = remoteConfigProvider.getString("OPENAI_API_KEY")
                if (apiKey.isEmpty()) {
                    throw IllegalStateException("OPENAI_API_KEY is empty")
                }
                chatCompletion = YChat.create(apiKey)
                    .chatCompletions()
                    .setModel("gpt-4o")
                    .setMaxTokens(300)
                    .setTemperature(1.0)
                    .setTopP(1.0)
                    .addMessage(
                        "system",
                        "You are a helpful chatbot but your mission is to sell MobileStack." +
                                " MobileStack is a kotlin multiplatform template that allows " +
                                "developers to ship mobile apps faster by including everything" +
                                " you need to launch and monetise a mobile app out of the box" +
                                " (db, billing, analytics, user management etc).  " +
                                "You can answer questions but always keep then short " +
                                "and find a way to promote MobileStack"
                    ) // Tip: add your own specialized system prompt
                    .setMaxResults(1)
                model.value = model.value.copy(loading = false)
            } catch (e: Exception) {
                Napier.e(e) { "Error creating YChat instance" }
                val errorMessage = (e.message
                    ?: "Unknown error") + ". Make sure you added your OpenAI key to Firebase remote configs with the name OPENAI_API_KEY."
                notificationProvider.showNotification(
                    Notification(
                        message = errorMessage,
                        duration = Duration.LONG
                    )
                )
            }
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
        scope.launch {// TIP: wrap network or long running operations in launch
            try {
                val result = chatCompletion
                    .execute(model.value.prompt) // TIP: change this for  executeWithoutMemory for stateless completions
                model.value = model.value.copy(
                    prompt = "",
                    messages = model.value.messages + model.value.prompt + (result.first().content.first() as Content.Text).text
                )
            } catch (e: Exception) {
                Napier.e(e) { "Error calling completions API" }
                notificationProvider.showNotification(
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
}