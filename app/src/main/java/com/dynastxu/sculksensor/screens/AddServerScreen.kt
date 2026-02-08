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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AddServerScreen(navController: NavController) {
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
            label = { Text("服务器名称") },
            modifier = Modifier.fillMaxWidth()
        )

        // 服务器地址输入框
        OutlinedTextField(
            value = serverAddress,
            onValueChange = { serverAddress = it },
            label = { Text("服务器地址") },
            modifier = Modifier.fillMaxWidth()
        )

        // 服务器端口输入框
        OutlinedTextField(
            value = serverPort,
            onValueChange = { serverPort = it },
            label = { Text("端口") },
            modifier = Modifier.fillMaxWidth()
        )

        // 保存按钮
        Button(
            onClick = {
                // TODO 保存逻辑
                navController.popBackStack() // 示例：返回上一页
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("保存")
        }
    }
}

@Preview
@Composable
fun AddServerScreenPreview() {
    AddServerScreen(rememberNavController())
}