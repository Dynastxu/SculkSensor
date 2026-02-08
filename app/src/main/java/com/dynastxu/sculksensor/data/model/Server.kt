package com.dynastxu.sculksensor.data.model

import java.util.UUID

data class Server(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val address: String,
    val port: Int = 25565,
    val version: String = "",
    val protocol: Int = 0,
    val icon: String = "",
    val playersMax: Int = 0,
    val playersOnline: Int = 0,
    val players: List<Player> = emptyList(),
    val description: String = "",
    val isOnline: Boolean = false,
    val delay: Long = 0,
    val lastChecked: Long? = null
)
