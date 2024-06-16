@file:OptIn(ExperimentalMaterial3Api::class)

package com.zenithapps.mobilestack.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.mohamedrejeb.calf.ui.datepicker.AdaptiveDatePicker
import com.mohamedrejeb.calf.ui.datepicker.rememberAdaptiveDatePickerState
import com.zenithapps.mobilestack.component.SignUpComponent
import com.zenithapps.mobilestack.ui.widget.FeatureItem
import com.zenithapps.mobilestack.ui.widget.MSFilledButton
import com.zenithapps.mobilestack.ui.widget.MSOutlinedTextField
import com.zenithapps.mobilestack.ui.widget.MSTopAppBar
import com.zenithapps.mobilestack.util.toLocalDateTime
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(component: SignUpComponent) {
    val coroutineScope = rememberCoroutineScope()
    val model by component.model.subscribeAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val interactionSource = remember { MutableInteractionSource() }
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )
    val state = rememberAdaptiveDatePickerState()

    LaunchedEffect(state.selectedDateMillis) {
        val selectedMillis = state.selectedDateMillis ?: return@LaunchedEffect
        component.onBirthdateChanged(selectedMillis.toLocalDateTime())
        scaffoldState.bottomSheetState.hide()
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MSTopAppBar(
                title = "Sign Up",
                onBackTap = component::onBackTap,
                actions = {
                    TextButton(onClick = component::onSignUpAnonymouslyTap) {
                        Text("Continue as Guest")
                    }
                }
            )
        },
        sheetPeekHeight = 0.dp,
        sheetContent = {
            AdaptiveDatePicker(
                modifier = Modifier.fillMaxSize(),
                state = state,
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        text = "Birthdate",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
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
                text = "Create an account to",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            FeatureItem("Try the boilerplate")
            FeatureItem("Manage your subscription")
            FeatureItem("Get notified with updates")
            Spacer(modifier = Modifier.weight(1f))
            Column {
                MSOutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = model.email,
                    onValueChange = { component.onEmailChanged(it) },
                    label = "Email*",
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
                    label = "Password*",
                    shouldHide = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    MSOutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            },
                        value = model.birthdate?.date?.toString() ?: "",
                        onValueChange = { },
                        label = "Birthdate",
                        readOnly = true
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp) // Remove fixed
                            .clickable {
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { component.onMarketingConsentChanged(!model.marketingConsent) },
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = model.marketingConsent,
                        onCheckedChange = { component.onMarketingConsentChanged(it) }
                    )
                    Text("I agree to receive marketing emails")
                }
                Spacer(modifier = Modifier.height(16.dp))
                MSFilledButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { component.onSignUpTap() },
                    text = "Sign Up",
                    loading = model.loading
                )
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { component.onSignInTap() }
                ) {
                    Text("Already have an account? Sign in")
                }
            }
        }
    }

}