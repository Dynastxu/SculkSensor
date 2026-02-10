package com.dynastxu.sculksensor.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val name: String,
    val id: String,
    var isOnline: Boolean? = null,
    var lastChecked: Long? = null,
    var lastOnline: Long? = null
)
