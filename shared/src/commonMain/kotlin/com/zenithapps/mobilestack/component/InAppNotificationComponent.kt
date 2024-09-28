package com.zenithapps.mobilestack.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.zenithapps.mobilestack.component.InAppNotificationComponent.Model
import com.zenithapps.mobilestack.provider.InAppNotificationProvider
import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification
import com.zenithapps.mobilestack.util.createCoroutineScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface InAppNotificationComponent {
    val model: Value<Model>

    data class Model(
        val notification: Notification? = null
    )

    fun onDismissNotification(notification: Notification)
}

class DefaultInAppNotificationComponent(
    componentContext: ComponentContext,
    inAppNotificationProvider: InAppNotificationProvider
) : InAppNotificationComponent, ComponentContext by componentContext {
    override val model = MutableValue(Model())

    private val scope = createCoroutineScope()

    init {
        inAppNotificationProvider.notifications
            .onEach { notification ->
                model.value =
                    model.value.copy(notification = notification)
            }
            .catch { e -> Napier.e { "Error collecting notifications: ${e.message}" } }
            .launchIn(scope)
    }

    override fun onDismissNotification(notification: Notification) {
        model.value = model.value.copy(notification = null)
    }
}

