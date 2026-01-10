package com.getmobilestack.kmp.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.getmobilestack.kmp.component.RootComponent
import com.getmobilestack.kmp.component.RootComponent.Child
import com.getmobilestack.kmp.ui.screen.home.AiChatScreen
import com.getmobilestack.kmp.ui.screen.onboarding.OnboardingScreen
import com.getmobilestack.kmp.ui.style.MobileStackTheme

@Composable
fun RootView(component: RootComponent) {
    MobileStackTheme {
        Surface {
            Box {
                Children(
                    modifier = Modifier.fillMaxSize(),
                    stack = component.stack
                ) {
                    when (val child = it.instance) {

                        is Child.Profile -> {
                            ProfileScreen(child.component)
                        }

                        is Child.SignUp -> {
                            SignUpScreen(child.component)
                        }

                        is Child.Loading -> {
                            LoadingScreen(child.component)
                        }

                        is Child.SignIn -> {
                            SignInScreen(child.component)
                        }

                        is Child.ResetPassword -> {
                            ResetPasswordScreen(child.component)
                        }

                        is Child.Welcome -> {
                            WelcomeScreen(child.component)
                        }

                        is Child.Home -> {
                            AiChatScreen(child.component)
                        }

                        is Child.RemotePaywall -> {
                            RemotePaywallScreen(child.component)
                        }

                        is Child.Onboarding -> {
                            OnboardingScreen(child.component)
                        }
                    }
                }
                InAppNotificationView(component.inAppNotificationComponent)
            }
        }
    }
}