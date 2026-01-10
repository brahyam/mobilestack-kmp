package com.getmobilestack.kmp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.getmobilestack.kmp.component.SignInComponent
import com.getmobilestack.kmp.ui.widget.FeatureItem
import com.getmobilestack.kmp.ui.widget.MSFilledButton
import com.getmobilestack.kmp.ui.widget.MSOutlinedTextField
import com.getmobilestack.kmp.ui.widget.MSTopAppBar

@Composable
fun SignInScreen(component: SignInComponent) {
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            MSTopAppBar(
                title = "Sign In",
                onBackTap = component::onBackTap
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
                .fillMaxSize()
                .padding(16.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Welcome back Entrepreneur",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "TODO:",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            FeatureItem("Ship")
            FeatureItem("Ship")
            FeatureItem("Ship")
            Spacer(modifier = Modifier.weight(1f))
            Column {
                MSOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = model.email,
                    onValueChange = { component.onEmailChanged(it) },
                    label = "Email",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(
                            FocusDirection.Down
                        )
                    })
                )
                Spacer(modifier = Modifier.height(8.dp))
                MSOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = model.password,
                    onValueChange = { component.onPasswordChanged(it) },
                    label = "Password",
                    shouldHide = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                        component.onSignInTap()
                    })
                )
                Spacer(modifier = Modifier.height(16.dp))
                MSFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { component.onSignInTap() },
                    text = "Sign In",
                    loading = model.loading
                )
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { component.onResetPasswordTap() }
                ) {
                    Text("Forgot your Password? Reset it.")
                }
            }
        }
    }

}