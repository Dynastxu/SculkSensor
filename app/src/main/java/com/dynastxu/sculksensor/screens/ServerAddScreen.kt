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
    var serverHost by remember { mutableStateOf("") }
    var serverPort by remember { mutableStateOf("") }
    var serverNameError by remember { mutableStateOf(false) }
    var serverHostError by remember { mutableStateOf(false) }
    var serverPortError by remember { mutableStateOf(false) }
    var serverNameErrorInfo by remember { mutableStateOf("") }
    var serverHostErrorInfo by remember { mutableStateOf("") }
    var serverPortErrorInfo by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxWidth(),
            isError = serverNameError,
            supportingText = {
                if (serverNameError) Text(serverNameErrorInfo)
            }
        )

        // 服务器地址输入框
        OutlinedTextField(
            value = serverHost,
            onValueChange = { serverHost = it },
            label = { Text(stringResource(R.string.label_server_host)) },
            modifier = Modifier.fillMaxWidth(),
            isError = serverHostError,
            supportingText = {
                if (serverHostError) Text(serverHostErrorInfo)
            }
        )

        // 服务器端口输入框
        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text(stringResource(R.string.label_server_port)) },
            modifier = Modifier.fillMaxWidth(),
            isError = serverPortError,
            supportingText = {
                if (serverPortError) Text(serverPortErrorInfo)
            },
            placeholder = { Text("25565") }
        )

        // 因为无法在 OnClick 里直接调用 stringResource ，所以写在外面
        val errorServerNameBlankString = stringResource(R.string.error_server_name_blank)
        val errorServerHostBlankString = stringResource(R.string.error_server_host_blank)
        val errorServerPortOutOfRangeString = stringResource(R.string.error_server_port_out_of_range)
        val errorServerPortNotIntString = stringResource(R.string.error_server_port_not_int)
        // 保存按钮
        Button(
            onClick = {
                // 检测名称
                if (serverName.isBlank()) {
                    serverNameError = true
                    serverNameErrorInfo = errorServerNameBlankString
                    return@Button
                } else {
                    serverNameError = false
                    serverNameErrorInfo = ""
                }
                // 检查主机地址
                if (serverHost.isBlank()) {
                    serverHostError = true
                    serverHostErrorInfo = errorServerHostBlankString
                    return@Button
                } else {
                    serverHostError = false
                    serverHostErrorInfo = ""
                }
                var port = 25565
                // 检查端口号
                if (serverPort.isNotBlank()) {
                    if (serverPort.toIntOrNull() == null) {
                        serverPortError = true
                        serverPortErrorInfo = errorServerPortNotIntString
                        return@Button
                    } else if (serverPort.toInt() !in 1..65535) {
                            serverPortError = true
                            serverPortErrorInfo = errorServerPortOutOfRangeString
                            return@Button
                    } else {
                        serverPortError = false
                        serverPortErrorInfo = ""
                        port = serverPort.toInt()
                    }
                }
                // 创建服务器数据
                val serverData = ServerData(
                    name = serverName,
                    host = serverHost,
                    port = port
                )

                // 保存到 DataStore
                viewModel.addServer(serverData)

                // 返回上一页
                navController.navigateUp()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(R.string.button_save))
        }
    }
}