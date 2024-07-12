/*
 * Copyright 2024 onseok
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.preat.peekaboo.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.annotation.FloatRange
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

internal object PeekabooImageResizer {
    internal fun resizeImage(
        imageByteArray: ByteArray,
        width: Int,
        height: Int,
        resizeThresholdBytes: Long,
        @FloatRange(from = 0.0, to = 1.0)
        compressionQuality: Double
    ): ByteArray? {
        return if (imageByteArray.size > resizeThresholdBytes) {
            resizeAndCompressImage(imageByteArray, width, height, compressionQuality)
        } else {
            imageByteArray
        }
    }

    private fun resizeAndCompressImage(
        imageByteArray: ByteArray,
        width: Int,
        height: Int,
        @FloatRange(from = 0.0, to = 1.0)
        compression: Double
    ): ByteArray? {
        val resizeCacheKey = "${imageByteArray.hashCode()}_w${width}_h$height"

        PeekabooBitmapCache.instance.get(resizeCacheKey)?.let { cachedBitmap ->
            return PeekabooBitmapCache.bitmapToByteArray(cachedBitmap)
        }

        val resizedBitmap = PeekabooBitmapCache.instance.get(resizeCacheKey) ?: run {
            ByteArrayInputStream(imageByteArray).use { inputStream ->
                val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeStream(inputStream, null, options)

                var inSampleSize = 1
                while (options.outWidth / inSampleSize > width || options.outHeight / inSampleSize > height) {
                    inSampleSize *= 2
                }

                options.inJustDecodeBounds = false
                options.inSampleSize = inSampleSize

                ByteArrayInputStream(imageByteArray).use { scaledInputStream ->
                    BitmapFactory.decodeStream(scaledInputStream, null, options)?.also { bitmap ->
                        PeekabooBitmapCache.instance.put(resizeCacheKey, bitmap)
                    }
                }
            }
        }

        resizedBitmap?.let {
            val rotatedBitmap = rotateImageIfRequired(it, imageByteArray)

            ByteArrayOutputStream().use { byteArrayOutputStream ->
                val validatedCompression = compression.coerceIn(0.0, 1.0)
                rotatedBitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    (100 * validatedCompression).toInt(),
                    byteArrayOutputStream
                )
                val byteArray = byteArrayOutputStream.toByteArray()
                PeekabooBitmapCache.instance.put(resizeCacheKey, rotatedBitmap)
                return byteArray
            }
        }

        return null
    }

    private fun rotateImageIfRequired(
        bitmap: Bitmap,
        imageByteArray: ByteArray
    ): Bitmap {
        val inputStream = ByteArrayInputStream(imageByteArray)
        val exif = ExifInterface(inputStream)
        val orientation =
            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1.0f, 1.0f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1.0f, -1.0f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.preScale(-1.0f, 1.0f)
                matrix.postRotate(270f)
            }

            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.preScale(-1.0f, 1.0f)
                matrix.postRotate(90f)
            }
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
