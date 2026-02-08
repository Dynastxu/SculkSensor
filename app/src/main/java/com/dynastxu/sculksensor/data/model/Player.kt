package com.dynastxu.sculksensor.data.model

data class Player(
    val name: String,
    val id: String,
    val isOnline: Boolean?,
    val lastChecked: Long?
)
