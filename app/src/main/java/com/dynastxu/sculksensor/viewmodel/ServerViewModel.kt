package com.dynastxu.sculksensor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynastxu.sculksensor.data.model.Server
import com.dynastxu.sculksensor.data.repository.ServerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ServerViewModel(private val repository: ServerRepository) : ViewModel() {

    // 服务器列表状态
    val servers: StateFlow<List<Server>> = repository.getServers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 添加新服务器
     */
    fun addServer(server: Server) {
        viewModelScope.launch {
            repository.addServer(server)
        }
    }

    /**
     * 删除服务器
     */
    fun deleteServer(serverId: UUID) {
        viewModelScope.launch {
            repository.deleteServer(serverId)
        }
    }
}