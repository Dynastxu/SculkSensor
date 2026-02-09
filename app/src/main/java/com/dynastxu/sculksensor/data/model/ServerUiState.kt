package com.dynastxu.sculksensor.data.model

import androidx.compose.runtime.MutableState

data class ServerUiState(
    val name: MutableState<String>,
    val host: MutableState<String>,
    val port: MutableState<Int>,
    val version: MutableState<String>,
    val protocol: MutableState<Int>,
    val icon: MutableState<String>,
    val playersMax: MutableState<Int>,
    val playersOnline: MutableState<Int>,
    val players: MutableState<List<PlayerData>>,
    val description: MutableState<String>,
    val isOnline: MutableState<Boolean>,
    val latency: MutableState<Long>,
    val lastChecked: MutableState<Long?>,
    val modLoader: MutableState<Int?>
)
