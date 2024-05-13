package com.zenithapps.mobilestack.ui.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MobileStackTheme(
    // TODO: Replace with useDarkTheme: Boolean = false, to use only light theme
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = getTypography(),
        content = content
    )
}