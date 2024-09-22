package com.zenithapps.mobilestack.ui.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zenithapps.mobilestack.resources.Res
import com.zenithapps.mobilestack.resources.img_app_icon
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import org.jetbrains.compose.resources.painterResource


@Composable
fun OnboardingStep4View(onDismissTap: () -> Unit) {
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
                text = "Ready to start shipping?",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enjoy MobileStack, and let us know in Discord if you need support.",
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
    }
}