package com.dynastxu.sculksensor.screens

import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.dynastxu.sculksensor.R
import com.dynastxu.sculksensor.ROUTE_ADD_SERVER
import com.dynastxu.sculksensor.data.model.ServerData
import com.dynastxu.sculksensor.viewmodel.ServerViewModel

const val TAG_SERVER_SCREEN_RENDERING = "服务器列表页面渲染"

@Composable
fun ServersScreen(navController: NavController, viewModel: ServerViewModel) {
    // 收集服务器列表
    val servers by viewModel.servers.collectAsState()

    // 更新服务器列表
    viewModel.updateServersUiStatus()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
        // 显示服务器列表
        LazyColumn {
            items(servers) { server ->
                Server(serverData = server, viewModel = viewModel)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AddServerButton(navController)
    }
}

@Composable
fun AddServerButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate(ROUTE_ADD_SERVER)
        }
    ) {
        Text(stringResource(R.string.button_add_server))
    }
}

@Composable
fun Server(serverData: ServerData, viewModel: ServerViewModel) {
    var expanded by remember { mutableStateOf(false) }

    val serverUiState = viewModel.serverUiStates[serverData.id]
    if (serverUiState == null) {
        Log.e(TAG_SERVER_SCREEN_RENDERING, "服务器列表与服务器 UI 列表不匹配（ UUID: ${serverData.id} ）")
        return
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onLongClick = {
                    expanded = true
                },
                onClick = {}
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图片（最左边）
            val base64String = serverUiState.icon.value

            // 去除非法字符
            val cleanedBase64String = base64String
                .substringAfter("base64,") // 去除 data:image/png;base64, 前缀
                .replace("\\u003d", "=")   // 解码 Unicode 转义字符

            // 解码 base64 字符串为字节数组
            val imageBytes = try {
                Base64.decode(cleanedBase64String, Base64.DEFAULT)
            } catch (e: IllegalArgumentException) {
                Log.e(TAG_SERVER_SCREEN_RENDERING, "服务器 ${serverData.host} 图片解析失败： $e")
                null // 如果解码失败，返回 null
            }
            Image(
                painter = if (imageBytes != null) {
                    // 使用 Coil 加载字节数组
                    rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageBytes)
                            .size(Size.ORIGINAL)
                            .build()
                    )
                } else {
                    // 解码失败时使用默认占位图
                    painterResource(R.drawable.ic_default_server)
                },
                contentDescription = "Server Icon",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(8.dp))

            // 中间部分
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 名称（上面）
                Text(
                    text = serverUiState.name.value,
                    style = MaterialTheme.typography.titleMedium
                )

                // 是否在线（圆点） + 延迟
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 是否在线（圆点）
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (serverUiState.isOnline.value) Color.Green else Color.Red,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 延迟
                    Text(
                        text = if (serverUiState.isOnline.value) "${serverUiState.latency.value}ms" else stringResource(R.string.text_offline),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 最右边部分
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // 在线人数
                Text(
                    text = if (serverUiState.isOnline.value) "${serverUiState.playersOnline.value}/${serverUiState.playersMax.value}" else "--/--",
                    style = MaterialTheme.typography.bodyMedium
                )

                // 版本
                Text(
                    text = if (serverUiState.isOnline.value) serverUiState.version.value else "--",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // 弹出菜单
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false } // 点击外部关闭菜单
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_item_delete)) },
                onClick = {
                    onDelete(serverData, viewModel) // 执行删除逻辑
                    expanded = false // 关闭菜单
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_item_refresh)) },
                onClick = {
                    onRefresh(serverData, viewModel)
                    expanded = false
                }
            )
        }
    }
    viewModel.updateServerState(serverData.id)
}

private fun onDelete(serverData: ServerData, viewModel: ServerViewModel) {
    viewModel.deleteServer(serverData.id)
}

private fun onRefresh(serverData: ServerData, viewModel: ServerViewModel) {
    viewModel.updateServerState(serverData.id)
}