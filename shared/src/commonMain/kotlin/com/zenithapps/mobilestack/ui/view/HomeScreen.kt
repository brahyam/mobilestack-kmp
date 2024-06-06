package com.zenithapps.mobilestack.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.zenithapps.mobilestack.component.SampleAiHomeComponent
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import com.zenithapps.mobilestack.ui.widget.MSOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: SampleAiHomeComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = component::onProfileTap
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = model.result ?: "",
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            MSOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = model.prompt,
                onValueChange = component::onPromptChanged,
                label = "Prompt"
            )
            MSFilledButton(
                loading = model.loading,
                modifier = Modifier.fillMaxWidth(),
                onClick = component::onSubmitTap,
                text = "Submit"
            )
        }
    }
}