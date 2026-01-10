package com.getmobilestack.kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.getmobilestack.kmp.component.RootComponent
import com.getmobilestack.kmp.ui.screen.RootView

fun createRootViewController(component: RootComponent) =
    ComposeUIViewController { RootView(component = component) }