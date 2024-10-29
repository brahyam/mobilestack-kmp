package com.zenithapps.mobilestack.ui.screen.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.preat.peekaboo.image.picker.toImageBitmap
import com.zenithapps.mobilestack.component.SampleAiHomeComponent
import com.zenithapps.mobilestack.ui.widget.Camera
import com.zenithapps.mobilestack.ui.widget.MSOutlinedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleAiHomeScreen(component: SampleAiHomeComponent) {
    val model by component.model.subscribeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                modifier = Modifier.padding(it).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        itemsIndexed(model.messages) { index, message ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = if (index % 2 == 0) Arrangement.Start else Arrangement.End
                            ) {
                                Card(
                                    modifier = Modifier.widthIn(max = 250.dp),
                                    colors = if (index % 2 == 0) CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    ) else CardDefaults.cardColors()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        if (message.image != null) {
                                            Image(
                                                bitmap = message.image.toImageBitmap(),
                                                contentDescription = "Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(100.dp)
                                            )
                                            if (message.text.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                        Text(
                                            text = message.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = CenterVertically
                ) {
                    if (model.image != null) {
                        Box {
                            OutlinedIconButton(
                                modifier = Modifier.size(60.dp).padding(top = 4.dp),
                                shape = MaterialTheme.shapes.medium,
                                onClick = component::onRemoveImageTap
                            ) {
                                Image(
                                    bitmap = model.image!!.toImageBitmap(),
                                    contentDescription = "Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Icon(
                                modifier = Modifier.padding(top = 4.dp),
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                    }
                    MSOutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = model.prompt,
                        singleLine = false,
                        onValueChange = component::onPromptChanged,
                        label = "Prompt"
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    if (model.prompt.isNotBlank() || model.image != null) {
                        OutlinedIconButton(
                            modifier = Modifier.size(60.dp).padding(top = 4.dp),
                            onClick = component::onSubmitTap,
                            enabled = (model.prompt.isNotBlank() || model.image != null) && !model.loading,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.Send,
                                contentDescription = "Send"
                            )
                        }
                    } else {
                        OutlinedIconButton(
                            modifier = Modifier.size(60.dp).padding(top = 4.dp),
                            onClick = component::onCameraTap,
                            enabled = !model.loading,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera"
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    if (model.capturing) {
        Camera(
            onCapture = component::onImageSelected,
            onSelectedFromFile = component::onImageSelected,
            onCloseTap = component::onCloseCameraTap,
            onRequestPermissionTap = component::onRequestCameraPermissionTap
        )
    }
}