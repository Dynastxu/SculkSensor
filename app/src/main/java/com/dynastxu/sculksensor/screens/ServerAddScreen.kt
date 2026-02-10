package com.dynastxu.sculksensor.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dynastxu.sculksensor.R
import com.dynastxu.sculksensor.data.model.ServerData
import com.dynastxu.sculksensor.viewmodel.ServerViewModel

@Composable
fun AddServerScreen(navController: NavController, viewModel: ServerViewModel) {
    var serverName by remember { mutableStateOf("") }
    var serverAddress by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 服务器名称输入框
        OutlinedTextField(
            value = serverName,
            onValueChange = { serverName = it },
            label = { Text(stringResource(R.string.label_server_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        // 服务器地址输入框
        OutlinedTextField(
            value = serverAddress,
            onValueChange = { serverAddress = it },
            label = { Text(stringResource(R.string.label_server_host)) },
            modifier = Modifier.fillMaxWidth()
        )

        // 服务器端口输入框
        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text(stringResource(R.string.label_server_port)) },
            modifier = Modifier.fillMaxWidth()
        )

        // 保存按钮
        Button(
            onClick = {
                if (serverName.isNotBlank() && serverAddress.isNotBlank()) {
                    // 创建 ServerListItem 对象（只存储用户输入的数据）
                    val serverData = ServerData(
                        name = serverName,
                        host = serverAddress,
                        port = serverPort.toIntOrNull() ?: 25565,
                    )

                    // 保存到 DataStore
                    viewModel.addServer(serverData)

                    // 返回上一页
                    navController.popBackStack()
                } else {
                    // 可以添加错误提示
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.button_save))
        }
    }
}