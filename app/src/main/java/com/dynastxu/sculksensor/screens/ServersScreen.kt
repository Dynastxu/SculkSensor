package com.dynastxu.sculksensor.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dynastxu.sculksensor.ROUTE_ADD_SERVER
import com.dynastxu.sculksensor.data.model.Server
import java.util.Base64
import java.util.UUID

@Composable
fun ServersScreen(navController: NavController) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
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
        Text("添加服务器")
    }
}

@Composable
fun Server(server: Server) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 图片（最左边）
        Image(
            painter = rememberAsyncImagePainter(server.icon),
            contentDescription = "Server Icon",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 中间部分：名称和版本
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 名称（上面）
            Text(
                text = server.name,
                style = MaterialTheme.typography.titleMedium
            )

            // 版本（下面）
            Text(
                text = server.version,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 最右边部分：是否在线和在线人数
        Column(
            horizontalAlignment = Alignment.End
        ) {
            // 第一行：是否在线（圆点） + 延迟
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 是否在线（圆点）
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (server.isOnline == true) Color.Green else Color.Red,
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(4.dp))

                // 延迟（圆点右边）
                Text(
                    text = "${server.delay}ms",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // 在线人数（下面）
            Text(
                text = "${server.playersOnline}/${server.playersMax}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ServerPreview() {
    // 创建一个示例 Server 对象
    val sampleServer = Server(
        id = UUID.randomUUID(),
        name = "Example Server",
        address = "192.168.1.1",
        port = 25565,
        version = "1.20.1",
        protocol = 763,
        icon = Base64.getEncoder().encodeToString(ByteArray(0)), // 空图标数据
        playersMax = 20,
        playersOnline = 5,
        players = emptyList(),
        description = "A sample Minecraft server",
        isOnline = true,
        delay = 50L, // 延迟为 50ms
        lastChecked = null
    )

    // 调用 Server Composable 函数
    Server(server = sampleServer)
}