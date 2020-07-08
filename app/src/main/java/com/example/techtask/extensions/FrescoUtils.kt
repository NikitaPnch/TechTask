package com.example.techtask.extensions

import android.content.Context
import android.net.Uri
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.RoundingParams
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder

fun SimpleDraweeView.createImageRequest(uri: String) {
    val request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
        .setResizeOptions(ResizeOptions(480, 480))
        .setRequestPriority(Priority.HIGH)
        .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
        .build()

    val roundingParams = RoundingParams
        .fromCornersRadius(16f)
        .setPaintFilterBitmap(true)

    this.hierarchy.roundingParams = roundingParams

    this.controller = Fresco.newDraweeControllerBuilder()
        .setOldController(this.controller)
        .setImageRequest(request)
        .build()
}

fun getDefaultMainDiskCacheConfig(context: Context): DiskCacheConfig? {
    return DiskCacheConfig.newBuilder(context)
        .setBaseDirectoryPathSupplier { context.applicationContext.cacheDir }
        .setBaseDirectoryName("image_cache")
        .setMaxCacheSize(80 * ByteConstants.MB.toLong())
        .setMaxCacheSizeOnLowDiskSpace(10 * ByteConstants.MB.toLong())
        .setMaxCacheSizeOnVeryLowDiskSpace(2 * ByteConstants.MB.toLong())
        .build()
}

fun getImagePipelineConfig(context: Context): ImagePipelineConfig? {
    return ImagePipelineConfig.newBuilder(context)
        .setMainDiskCacheConfig(getDefaultMainDiskCacheConfig(context))
        .build()
}