package com.dynastxu.sculksensor.data.model

import java.util.UUID

data class Server(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val address: String,
    val port: Int = 25565,
    val version: String,
    val protocol: Int,
    val icon: String?,
    val playersMax: Int,
    val playersOnline: Int,
    val players: List<Player>,
    val description: String?,
    val isOnline: Boolean?,
    val delay: Long?,
    val lastChecked: Long?
)
