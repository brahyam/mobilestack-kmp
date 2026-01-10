package com.getmobilestack.kmp.ui.screen.onboarding

import OnboardingStep1View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions
import com.getmobilestack.kmp.component.OnboardingComponent
import com.getmobilestack.kmp.ui.widget.MSTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(component: OnboardingComponent) {
    val model by component.model.subscribeAsState()
    val pagerState = rememberPagerState(pageCount = { model.steps.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(model.currentStep) {
        if (model.currentStep != pagerState.currentPage) {
            scope.launch {
                pagerState.animateScrollToPage(model.currentStep)
            }
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (model.currentStep != page) {
                component.onPageSelected(page)
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                MSTopAppBar(
                    title = "",
                    onBackTap = if (model.currentStep > 0 && model.currentStep < model.steps.size - 1) component::onBackTap else null,
                    actions = {
                        if (model.currentStep > 0 && model.currentStep < model.steps.size - 1) {
                            TextButton(onClick = { component.onSkipTap() }) {
                                Text("Skip all")
                            }
                        }
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LinearProgressIndicator(
                        progress = {
                            model.currentStep.toFloat() / (model.steps.size - 1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.padding(it),
                    ) { page ->
                        model.steps[page].let { step ->
                            when (step) {
                                OnboardingComponent.Step.WELCOME -> OnboardingStep0View(
                                    onNextTap = component::onNextTap,
                                    onHapticFeedback = component::onHapticFeedback
                                )
                                OnboardingComponent.Step.STEP_1 -> OnboardingStep1View(onNextTap = component::onNextTap)
                                OnboardingComponent.Step.STEP_2 -> OnboardingStep2View(onNextTap = component::onNextTap)
                                OnboardingComponent.Step.STEP_3 -> OnboardingStep3View(onNextTap = component::onNextTap)
                                OnboardingComponent.Step.REVIEW -> OnboardingStep4View(onDismissTap = component::onNextTap)
                                OnboardingComponent.Step.PAYWALL -> OnboardingStep4View(onDismissTap = component::onNextTap)
                            }
                        }
                    }
                }
            }
        )
        if (model.shouldShowPaywall && model.currentStep == model.steps.size - 1) {
            val options = remember {
                PaywallOptions(dismissRequest = component::onNextTap) {
                    shouldDisplayDismissButton = true
                }
            }
            Paywall(options)
        }
    }

}





