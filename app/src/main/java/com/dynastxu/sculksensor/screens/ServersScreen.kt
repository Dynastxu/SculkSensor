package com.dynastxu.sculksensor.screens

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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.dynastxu.sculksensor.R
import com.dynastxu.sculksensor.ROUTE_ADD_SERVER
import com.dynastxu.sculksensor.data.model.Server
import com.dynastxu.sculksensor.viewmodel.ServerViewModel
import java.util.Base64
import java.util.UUID

@Composable
fun ServersScreen(navController: NavController, viewModel: ServerViewModel) {
    // 收集服务器列表
    val servers by viewModel.servers.collectAsState()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
        // 显示服务器列表
        LazyColumn {
            items(servers) { server ->
                Server(server = server, viewModel = viewModel)
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
fun Server(server: Server, viewModel: ServerViewModel) {
    var expanded by remember { mutableStateOf(false) }

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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图片（最左边）
            Image(
                painter = rememberAsyncImagePainter(
                    model = server.icon,
                    placeholder = painterResource(R.drawable.ic_default_server),
                    error = painterResource(R.drawable.ic_default_server)
                ),
                contentDescription = "Server Icon",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),

            )

            Spacer(modifier = Modifier.width(8.dp))

            // 中间部分
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 名称（上面）
                Text(
                    text = server.name,
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
                                color = if (server.isOnline) Color.Green else Color.Red,
                                shape = CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 延迟
                    Text(
                        text = "${server.delay}ms",
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
                    text = "${server.playersOnline}/${server.playersMax}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // 版本
                Text(
                    text = server.version,
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
                    onDelete(server, viewModel) // 执行删除逻辑
                    expanded = false // 关闭菜单
                }
            )
        }
    }
}

fun onDelete(server: Server, viewModel: ServerViewModel) {
    viewModel.deleteServer(server.id)
}