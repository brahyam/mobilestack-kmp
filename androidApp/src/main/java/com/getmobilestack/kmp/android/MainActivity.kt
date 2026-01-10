package com.getmobilestack.kmp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.arkivanov.decompose.defaultComponentContext
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.getmobilestack.kmp.android.provider.AndroidCapabilityProvider
import com.getmobilestack.kmp.android.provider.FirebaseAnalyticsProvider
import com.getmobilestack.kmp.component.DefaultRootComponent
import com.getmobilestack.kmp.ui.screen.RootView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootComponent = DefaultRootComponent(
            componentContext = defaultComponentContext(),
            osCapabilityProvider = AndroidCapabilityProvider(this),
            analyticsProvider = FirebaseAnalyticsProvider(Firebase.analytics),
        )
        setContent {
            RootView(rootComponent)
        }
    }
}
