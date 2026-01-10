package com.getmobilestack.kmp.util

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import platform.Foundation.NSData
import platform.Foundation.create
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
actual fun processImage(bytes: ByteArray): ByteArray {
    val data = bytes.toData()
    val image = data.let { UIImage.imageWithData(it) }
    val jpegData = image?.let { UIImageJPEGRepresentation(it, 1.00) }
    val jpegByteArray = jpegData?.length?.let {
        ByteArray(it.toInt()).apply {
            memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
        }
    }
    return jpegByteArray!!
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toData(): NSData = memScoped {
    val data = NSData.create(
        bytes = allocArrayOf(this@toData),
        length = this@toData.size.toULong()
    )
    return data
}