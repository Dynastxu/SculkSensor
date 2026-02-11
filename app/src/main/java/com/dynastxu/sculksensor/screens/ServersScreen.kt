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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.dynastxu.sculksensor.R
import com.dynastxu.sculksensor.ROUTE_ADD_SERVER
import com.dynastxu.sculksensor.ROUTE_IMAGE
import com.dynastxu.sculksensor.ROUTE_SERVER_DETAILS
import com.dynastxu.sculksensor.TAG_SERVERS_SCREEN_RENDERING
import com.dynastxu.sculksensor.data.model.ServerUiState
import com.dynastxu.sculksensor.viewmodel.ImageViewModel
import com.dynastxu.sculksensor.viewmodel.ServerViewModel
import java.util.UUID

private val Color.Companion.LightBlue: Color
    get() = Color(0xFFB2EBF2)

@Composable
fun ServersScreen(navController: NavController, viewModel: ServerViewModel) {
    // 收集服务器列表
    val servers by viewModel.servers.collectAsState()

    // 更新服务器列表
    viewModel.updateServersUiStatus()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
        // 显示服务器列表
        LazyColumn {
            items(servers) { server ->
                ServerListItem(server.id, serverViewModel = viewModel, navController = navController)
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
fun ServerListItem(
    serverId: UUID,
    serverViewModel: ServerViewModel,
    imageViewModel: ImageViewModel? = null,
    navController: NavController,
    clickable: Boolean = true,
    clipImage: Boolean = true,
    showDescription: Boolean = false,
    isImageClickable: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val serverUiState = serverViewModel.serverUiStates[serverId]
    if (serverUiState == null) {
        Log.e(TAG_SERVERS_SCREEN_RENDERING, "服务器列表与服务器 UI 列表不匹配（ UUID: $serverId ）")
        return
    }

    serverViewModel.updateServerState(serverId)
    serverUiState.isGettingStatue.value = true

    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    Card(
        modifier = if (clickable) cardModifier
            .combinedClickable(
                onLongClick = {
                    expanded = true
                },
                onClick = {
                    serverViewModel.selectedServerId = serverId
                    navController.navigate(ROUTE_SERVER_DETAILS)
                }
            ) else cardModifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 图片
                ServerImage(
                    serverUiState = serverUiState,
                    clipImage = clipImage,
                    clickable = isImageClickable,
                    viewModel = imageViewModel,
                    navController = navController
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 8.dp, 8.dp, if (showDescription) 0.dp else 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                if (serverUiState.isGettingStatue.value) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(12.dp), // 设置大小
                                        strokeWidth = 4.dp              // 设置线条粗细
                                    )
                                } else {
                                    // 是否在线（圆点）
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(
                                                color = if (serverUiState.isOnline.value) Color.Green else Color.Red,
                                                shape = CircleShape
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // 延迟
                                Text(
                                    text = if (serverUiState.isOnline.value) "${if (serverUiState.latency.value > 0) serverUiState.latency.value else "--"}ms" else stringResource(
                                        R.string.text_offline
                                    ),
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

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 版本
                                Text(
                                    text = if (serverUiState.isOnline.value) serverUiState.version.value else "--",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Mod 加载器
                                Text(
                                    text = if (serverUiState.modLoader.value == null) "" else stringResource(
                                        serverUiState.modLoader.value!!
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (showDescription) {
                        Text(
                            text = parseMinecraftFormatting(serverUiState.description.value),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .align(Alignment.Start)
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp)
                        )
                    }
                }
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
                    onDelete(serverId, serverViewModel) // 执行删除逻辑
                    expanded = false // 关闭菜单
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.menu_item_refresh)) },
                onClick = {
                    onRefresh(serverId, serverViewModel)
                    expanded = false
                }
            )
        }
    }
}

private fun onDelete(serverId: UUID, viewModel: ServerViewModel) {
    viewModel.deleteServer(serverId)
}

private fun onRefresh(serverId: UUID, viewModel: ServerViewModel) {
    viewModel.updateServerState(serverId)
}

@Composable
fun ServerImage(
    serverUiState: ServerUiState,
    clipImage: Boolean,
    clickable: Boolean,
    viewModel: ImageViewModel? = null,
    navController: NavController? = null
) {
    // 图片
    val base64String = serverUiState.icon.value

    var imageBytes: ByteArray? = null
    if (base64String.isNotBlank()) {
        // 去除非法字符
        val cleanedBase64String = base64String
            .substringAfter("base64,") // 去除 data:image/png;base64, 前缀
            .replace("\\u003d", "=")   // 解码 Unicode 转义字符

        // 解码 base64 字符串为字节数组
        imageBytes = try {
            Base64.decode(cleanedBase64String, Base64.DEFAULT)
        } catch (e: IllegalArgumentException) {
            Log.e(
                TAG_SERVERS_SCREEN_RENDERING,
                "服务器 ${serverUiState.host} 图片解析失败： $e"
            )
            null // 如果解码失败，返回 null
        }
    }
    val imageModifier = Modifier
        .size(64.dp).combinedClickable(
            onClick = {
                if (!clickable) return@combinedClickable
                if (imageBytes == null) return@combinedClickable
                viewModel?.imageData = imageBytes
                navController?.navigate(ROUTE_IMAGE)
            }
        )
    Image(
        painter = if (imageBytes != null) {
            // 使用 Coil 加载字节数组
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageBytes)
                    .size(Size.ORIGINAL)
                    .placeholder(R.drawable.ic_default_server)
                    .build()
            )
        } else {
            // 解码失败时使用默认占位图
            painterResource(R.drawable.ic_default_server)
        },
        contentDescription = "Server Icon",
        modifier = if (clipImage) imageModifier
            .clip(CircleShape) else imageModifier
    )
}

@Composable
private fun parseMinecraftFormatting(text: String): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        var currentIndex = 0
        var currentColor: Color = Color.Unspecified
        var isBold = false
        var isItalic = false
        var isUnderline = false

        while (currentIndex < text.length) {
            val char = text[currentIndex]
            if (char == '§' && currentIndex + 1 < text.length) {
                // 处理格式化代码
                val code = text[currentIndex + 1]
                when (code) {
                    '0' -> currentColor = Color.Black
                    '1' -> currentColor = Color.Blue
                    '2' -> currentColor = Color.Green
                    '3' -> currentColor = Color.Cyan
                    '4' -> currentColor = Color.Red
                    '5' -> currentColor = Color.Magenta
                    '6' -> currentColor = Color.Yellow
                    '7' -> currentColor = Color.Gray
                    '8' -> currentColor = Color.DarkGray
                    '9' -> currentColor = Color.LightBlue
                    'a' -> currentColor = Color.Green
                    'b' -> currentColor = Color.Cyan
                    'c' -> currentColor = Color.Red
                    'd' -> currentColor = Color.Magenta
                    'e' -> currentColor = Color.Yellow
                    'f' -> currentColor = Color.White
                    'l' -> isBold = true
                    'o' -> isItalic = true
                    'n' -> isUnderline = true
                    'r' -> {
                        // 重置样式
                        currentColor = Color.Unspecified
                        isBold = false
                        isItalic = false
                        isUnderline = false
                    }
                }
                currentIndex += 2 // 跳过 § 和后面的字符
            } else {
                // 添加普通字符
                withStyle(
                    style = SpanStyle(
                        color = currentColor,
                        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if (isUnderline) TextDecoration.Underline else null
                    )
                ) {
                    append(char)
                }
                currentIndex++
            }
        }
    }
    return annotatedString
}
