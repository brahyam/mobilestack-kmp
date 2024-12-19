package com.zenithapps.mobilestack.android.provider

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.content.FileProvider
import com.google.android.play.core.review.ReviewManagerFactory
import com.zenithapps.mobilestack.provider.OSCapabilityProvider
import com.zenithapps.mobilestack.provider.OSCapabilityProvider.VibrationStrength
import java.io.File

class AndroidCapabilityProvider(private val activity: Activity) : OSCapabilityProvider {
    override fun openUrl(url: String) {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun getPlatform(): OSCapabilityProvider.Platform {
        return OSCapabilityProvider.Platform.ANDROID
    }

    override fun getAppVersion(): String {
        return activity.getPackageInfo().versionName
    }

    override fun managePurchases() {
        openUrl("https://play.google.com/store/account/subscriptions")
    }

    override fun openAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
        with(intent) {
            data = Uri.fromParts("package", activity.packageName, null)
            addCategory(CATEGORY_DEFAULT)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_HISTORY)
            addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }
        activity.startActivity(intent)
    }

    override fun requestStoreReview() {
        val reviewManager = ReviewManagerFactory.create(activity)
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // Do nothing
                }
            }
        }
    }

    override fun shareImage(
        imageByteArray: ByteArray,
        mimeType: String,
        title: String,
        message: String,
    ) {
        val imagePath = File(activity.cacheDir, "images")
        imagePath.mkdirs()
        val file = File(imagePath, "shared_image.jpg")
        file.outputStream().use { it.write(imageByteArray) }
        val imageUri = FileProvider.getUriForFile(
            activity,
            "${activity.application.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, message)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(Intent.createChooser(intent, title))
    }

    override fun vibrate(durationMs: Long, strength: VibrationStrength) {
        val amplitude = when (strength) {
            VibrationStrength.LIGHT -> 1
            VibrationStrength.MEDIUM -> 128
            VibrationStrength.STRONG -> 255
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                activity.applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.vibrate(
                CombinedVibration.createParallel(
                    VibrationEffect.createOneShot(
                        durationMs,
                        amplitude
                    )
                )
            )
        } else {
            val vibrator =
                activity.applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        amplitude
                    )
                )
            } else {
                vibrator.vibrate(durationMs)
            }
        }
    }

    private fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }
}