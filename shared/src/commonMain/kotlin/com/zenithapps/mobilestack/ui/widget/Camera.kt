package com.zenithapps.mobilestack.ui.widget

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.SwitchCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import com.preat.peekaboo.image.picker.ResizeOptions as PickerResizeOptions
import com.preat.peekaboo.ui.camera.ResizeOptions as CameraResizeOptions

val pickerResizeOptions = PickerResizeOptions(
    width = 512, // Custom width
    height = 512, // Custom height
    resizeThresholdBytes = 2 * 1024 * 1024L, // Custom threshold for 2MB,
    compressionQuality = 0.5 // Adjust compression quality (0.0 to 1.0)
)

private val cameraResizeOptions = CameraResizeOptions(
    width = 512, // Custom width
    height = 512, // Custom height
    resizeThresholdBytes = 2 * 1024 * 1024L, // Custom threshold for 2MB,
    compressionQuality = 0.5 // Adjust compression quality (0.0 to 1.0)
)

@Composable
fun Camera(
    onCapture: (ByteArray) -> Unit,
    onSelectedFromFile: (ByteArray) -> Unit,
    onCloseTap: () -> Unit,
    onRequestPermissionTap: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cameraState = rememberPeekabooCameraState(onCapture = {
        if (it != null) {
            onCapture(it)
        }
    })
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        resizeOptions = pickerResizeOptions,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                onSelectedFromFile(it)
            }
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        PeekabooCamera(
            state = cameraState,
            modifier = Modifier.fillMaxSize(),
            resizeOptions = cameraResizeOptions,
            permissionDeniedContent = {
                Surface {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Camera permission is required.",
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Please grant the camera permission or select an image from your files to continue.",
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        MSFilledButton(
                            text = "Grant Camera Permissions",
                            onClick = onRequestPermissionTap
                        )
                    }
                }
            },
        )

        if (cameraState.isCapturing) {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Saving your image...",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CameraButton(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    enabled = !cameraState.isCapturing,
                    onClick = onCloseTap
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CameraButton(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Browse gallery",
                        enabled = !cameraState.isCapturing,
                        onClick = {
                            singleImagePicker.launch()
                        }
                    )
                    CameraButton(
                        imageVector = Icons.Default.Circle,
                        contentDescription = "Capture",
                        enabled = !cameraState.isCapturing && cameraState.isCameraReady,
                        onClick = {
                            cameraState.capture()
                        }
                    )
                    CameraButton(
                        imageVector = Icons.Default.SwitchCamera,
                        contentDescription = "Switch camera",
                        enabled = !cameraState.isCapturing && cameraState.isCameraReady,
                        onClick = {
                            cameraState.toggleCamera()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraButton(
    imageVector: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier.size(52.dp),
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.Black.copy(alpha = 0.5f),
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription
            )
        }
    }
}