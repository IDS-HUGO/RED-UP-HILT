package com.hugodev.red_up.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream

fun compressImageForUpload(
    context: Context,
    uri: Uri,
    maxDimension: Int = 1280,
    targetBytes: Int = 900 * 1024
): ByteArray? {
    val tag = "ImageCompressionUtils"
    Log.d(tag, "Starting compression uri=$uri targetBytes=$targetBytes maxDimension=$maxDimension")
    val originalBytes = context.contentResolver.openInputStream(uri)?.use { stream ->
        stream.readBytes()
    } ?: run {
        Log.e(tag, "Unable to open input stream for uri=$uri (raw bytes)")
        return null
    }
    Log.d(tag, "Read raw bytes uri=$uri size=${originalBytes.size}")

    if (originalBytes.size <= targetBytes) {
        Log.d(tag, "Raw bytes already within target. Skipping compression uri=$uri")
        return originalBytes
    }

    val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size, boundsOptions)

    // Fallback for providers/codecs that fail Bitmap decoding but still provide bytes.
    if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) {
        Log.w(tag, "Bounds decode failed for uri=$uri, truncating raw bytes fallback")
        return originalBytes.copyOf(minOf(originalBytes.size, targetBytes))
    }

    var inSampleSize = 1
    while ((boundsOptions.outWidth / inSampleSize) > maxDimension || (boundsOptions.outHeight / inSampleSize) > maxDimension) {
        inSampleSize *= 2
    }

    val decodeOptions = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
    val bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size, decodeOptions)
    if (bitmap == null) {
        Log.w(tag, "Bitmap decode returned null uri=$uri sample=$inSampleSize; using raw truncation fallback")
        return originalBytes.copyOf(minOf(originalBytes.size, targetBytes))
    }

    val output = ByteArrayOutputStream()
    var quality = 90
    do {
        output.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output)
        quality -= 10
    } while (output.size() > targetBytes && quality >= 40)

    bitmap.recycle()
    Log.d(tag, "Compression finished uri=$uri resultBytes=${output.size()} finalQuality=${quality + 10} sampleSize=$inSampleSize")
    return output.toByteArray()
}
