package com.zenithapps.mobilestack.provider

import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface InAppNotificationProvider {

    val notifications: Flow<Notification>
    fun showNotification(notification: Notification)

    data class Notification(
        val message: String,
        val action: Action? = null,
        val withDismissAction: Boolean = true,
        val duration: Duration = if (action == null) Duration.SHORT else Duration.INDEFINITE
    ) {
        enum class Duration {
            SHORT,
            LONG,
            INDEFINITE
        }

        data class Action(
            val title: String,
            val onClick: () -> Unit
        )
    }
}

class DefaultInAppNotificationProvider : InAppNotificationProvider {

    override val notifications = MutableSharedFlow<Notification>(replay = 1)

    override fun showNotification(notification: Notification) {
        notifications.tryEmit(notification)
    }
}