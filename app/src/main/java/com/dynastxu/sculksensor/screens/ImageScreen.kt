package com.dynastxu.sculksensor.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.dynastxu.sculksensor.R
import com.dynastxu.sculksensor.TAG_IMAGE_SCREEN_RENDERING
import com.dynastxu.sculksensor.viewmodel.ImageViewModel
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ImageScreen(navController: NavController, viewModel: ImageViewModel) {
    when (val imageData = viewModel.imageData) {
        is Painter -> ImageScreen(imageData, navController)
        is Int -> ImageScreen(imageData, navController)
        is ByteArray -> ImageScreen(imageData, navController)
        else -> {
            if (imageData == null) {
                Log.e(TAG_IMAGE_SCREEN_RENDERING, "图片数据为 null")
            } else {
                Log.e(
                    TAG_IMAGE_SCREEN_RENDERING,
                    "不支持的图片数据类型： ${imageData::class.simpleName}"
                )
            }
            ImageScreen(navController)
        }
    }
}

@Composable
private fun ImageScreen(painter: Painter, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {
        val zoomState = rememberZoomState(contentSize = painter.intrinsicSize)
        Image(
            painter = painter,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .combinedClickable(
                    onClick = {
                        navController.navigateUp()
                    }
                )
                .zoomable(zoomState),
            contentDescription = "Image Review"
        )
    }
}

/**
 * 当没有图片可以展示时调用
 */
@Composable
private fun ImageScreen(navController: NavController){
    ImageScreen(R.drawable.img_default, navController)
}

@Composable
private fun ImageScreen(resourceId: Int, navController: NavController) {
    ImageScreen(
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(resourceId)
                .size(Size.ORIGINAL)
                .placeholder(R.drawable.img_default)
                .build()
        ),
        navController
    )
}

@Composable
private fun ImageScreen(imageBytes: ByteArray, navController: NavController) {
    ImageScreen(
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageBytes)
                .size(Size.ORIGINAL)
                .placeholder(R.drawable.img_default)
                .build()
        ),
        navController
    )
}