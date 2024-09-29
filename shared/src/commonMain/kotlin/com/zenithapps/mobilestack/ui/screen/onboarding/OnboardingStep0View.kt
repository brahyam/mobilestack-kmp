package com.zenithapps.mobilestack.ui.screen.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zenithapps.mobilestack.resources.Res
import com.zenithapps.mobilestack.resources.img_app_icon
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource


@Composable
fun OnboardingStep0View(socialProofInteractions: Int, onNextTap: () -> Unit) {

    val welcomeSocialMessage = listOf(
        "...ready to ship",
        "...well documented",
        "...5x faster setup",
        "...game changer",
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "MobileStack",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Validate ideas and monetize FAST",
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
        Text(
            text = "$socialProofInteractions users have shipped with MobileStack",
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