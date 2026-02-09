package com.dynastxu.sculksensor.data.model

data class PlayerData(
    val name: String,
    val id: String,
    var isOnline: Boolean?,
    var lastChecked: Long?
)
