package com.dynastxu.sculksensor.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dynastxu.sculksensor.TAG_SERVER_DETAILS_SCREEN_RENDERING
import com.dynastxu.sculksensor.viewmodel.ServerViewModel

@Composable
fun ServerDetailsScreen(navController: NavController, viewModel: ServerViewModel) {
    if (viewModel.selectedServerId == null) {
        Log.e(TAG_SERVER_DETAILS_SCREEN_RENDERING, "未选择服务器")
        return
    }
    val server = viewModel.serverUiStates.getValue(viewModel.selectedServerId!!)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
        ServerListItem(
            serverId = viewModel.selectedServerId!!,
            viewModel = viewModel,
            navController = navController,
            clickable = false,
            clipImage = false
        )
    }
}
