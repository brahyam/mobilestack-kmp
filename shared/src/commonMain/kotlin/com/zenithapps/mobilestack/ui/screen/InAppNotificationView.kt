package com.zenithapps.mobilestack.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.zenithapps.mobilestack.component.InAppNotificationComponent
import com.zenithapps.mobilestack.provider.InAppNotificationProvider.Notification

@Composable
fun InAppNotificationView(component: InAppNotificationComponent) {
    val model by component.model.subscribeAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(model.notification) {
        model.notification?.let {
            snackbarHostState.showSnackbar(
                message = it.message,
                actionLabel = it.action?.title,
                withDismissAction = it.withDismissAction,
                duration = when (it.duration) {
                    Notification.Duration.SHORT -> SnackbarDuration.Short
                    Notification.Duration.LONG -> SnackbarDuration.Long
                    Notification.Duration.INDEFINITE -> SnackbarDuration.Indefinite
                }
            ).run {
                when (this) {
                    SnackbarResult.Dismissed -> component.onDismissNotification(it)
                    SnackbarResult.ActionPerformed -> it.action?.onClick?.invoke()
                }
            }
        }
    }
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.fillMaxWidth()
    )
}