package com.zenithapps.mobilestack.ui.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.zenithapps.mobilestack.component.ResetPasswordComponent
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import com.zenithapps.mobilestack.ui.widget.MSOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(component: ResetPasswordComponent) {
    val model by component.model.subscribeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Reset Password")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { component.onBackTap() }) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize().padding(16.dp)
        ) {
            MSOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = model.email,
                onValueChange = { component.onEmailChanged(it) },
                label = "Email",
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    keyboardController?.hide()
                    component.onResetPasswordTap()
                })
            )
            MSFilledButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { component.onResetPasswordTap() },
                text = "Reset Password",
                loading = model.loading
            )
        }
    }
}