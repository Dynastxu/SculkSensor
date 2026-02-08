package com.dynastxu.sculksensor.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ServersScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text("服务器内容", modifier = Modifier.padding(24.dp))
    }
}