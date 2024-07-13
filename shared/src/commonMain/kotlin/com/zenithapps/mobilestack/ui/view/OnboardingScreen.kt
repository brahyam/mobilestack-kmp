package com.zenithapps.mobilestack.ui.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.mmk.kmprevenuecat.purchases.data.CustomerInfo
import com.mmk.kmprevenuecat.purchases.ui.Paywall
import com.mmk.kmprevenuecat.purchases.ui.PaywallListener
import com.zenithapps.mobilestack.component.OnboardingComponent
import com.zenithapps.mobilestack.resources.Res
import com.zenithapps.mobilestack.resources.img_app_icon
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import com.zenithapps.mobilestack.ui.widget.MSTopAppBar
import com.zenithapps.mobilestack.util.now
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.jetbrains.compose.resources.painterResource

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
                component.onPageSelected(page) // Implement this method to update the model's current step
            }
        }
    }

    Scaffold(
        topBar = {
            MSTopAppBar(
                title = "",
                onBackTap = if (model.currentStep > 0 && model.currentStep < model.steps.size - 1) component::onBackTap else null,
                actions = {
                    if (model.currentStep < model.steps.size - 1) {
                        TextButton(onClick = { component.onSkipTap() }) {
                            Text("Skip")
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
                            OnboardingComponent.Step.WELCOME -> WelcomeScreen(onNextTap = component::onNextTap)
                            OnboardingComponent.Step.STEP_1 -> OnboardingStep1Screen(onNextTap = component::onNextTap)
                            OnboardingComponent.Step.STEP_2 -> OnboardingStep2Screen(onNextTap = component::onNextTap)
                            OnboardingComponent.Step.STEP_3 -> OnboardingStep3Screen(onNextTap = component::onNextTap)
                            OnboardingComponent.Step.PAYWALL -> PaywallScreen(onDismissTap = component::onNextTap)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeScreen(onNextTap: () -> Unit) {

    val welcomeSocialMessage = listOf(
        "...made it easier",
        "...its funny",
        "...saved me time",
        "...it's addictive",
    )

    var messageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            messageIndex = (messageIndex + 1) % welcomeSocialMessage.size
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(54.dp))
            Image(
                painter = painterResource(Res.drawable.img_app_icon),
                contentDescription = "App logo",
                modifier = Modifier.width(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Welcome to XXXX.",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Main value proposition.",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            AnimatedContent(
                targetState = welcomeSocialMessage[messageIndex],
                transitionSpec = {
                    fadeIn(animationSpec = tween(1000)) togetherWith fadeOut(
                        animationSpec = tween(
                            1000
                        )
                    )
                }
            ) { message ->
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    text = "\"$message\"",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            Row {
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "Star",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        val startDate = LocalDate(2024, 7, 1)
        val currentDate = now()
        val daysBetween = startDate.daysUntil(currentDate.date)
        val appMainFeatureUsage = daysBetween * 43
        Text(
            text = "Users have done $appMainFeatureUsage XXX",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        MSFilledButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Next",
            onClick = onNextTap
        )
    }
}

@Composable
fun OnboardingStep1Screen(onNextTap: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.width(200.dp),
                painter = painterResource(Res.drawable.img_app_icon),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Step 1 title.",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Step 1 description.",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        MSFilledButton(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            text = "Continue",
            onClick = onNextTap
        )
    }
}

@Composable
fun OnboardingStep2Screen(onNextTap: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.width(200.dp),
                painter = painterResource(Res.drawable.img_app_icon),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Step 2 Title.",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Step 2 description",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        MSFilledButton(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            text = "Next",
            onClick = onNextTap
        )
    }
}

@Composable
fun OnboardingStep3Screen(onNextTap: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                modifier = Modifier.width(200.dp),
                painter = painterResource(Res.drawable.img_app_icon),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Step 3 Title.",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Step 3 description.",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        MSFilledButton(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            text = "Next",
            onClick = onNextTap
        )
    }
}

@Composable
fun PaywallScreen(onDismissTap: () -> Unit) {
    Box {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Image(
                painter = painterResource(Res.drawable.img_app_icon),
                contentDescription = "App logo",
                modifier = Modifier.height(200.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Paywall Title.",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Paywall description.",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            MSFilledButton(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                text = "Get Started",
                onClick = onDismissTap
            )
        }
        Paywall(
            shouldDisplayDismissButton = true,
            onDismiss = onDismissTap,
            listener = object : PaywallListener {
                override fun onPurchaseStarted() {

                }

                override fun onPurchaseCompleted(customerInfo: CustomerInfo?) {
                    onDismissTap()
                }

                override fun onPurchaseError(error: String?) {

                }

                override fun onPurchaseCancelled() {

                }

                override fun onRestoreStarted() {

                }

                override fun onRestoreCompleted(customerInfo: CustomerInfo?) {
                    onDismissTap()
                }

                override fun onRestoreError(error: String?) {

                }
            }
        )
    }

}