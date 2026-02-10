package com.dynastxu.sculksensor.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dynastxu.sculksensor.data.model.ServerData
import com.dynastxu.sculksensor.data.model.ServerUiState
import com.dynastxu.sculksensor.data.repository.ServerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ServerViewModel(private val repository: ServerRepository) : ViewModel() {
    private val TAG_SERVER_VIEW_MODEL = "ServerViewModel"
    // 服务器列表状态
    val servers: StateFlow<List<ServerData>> = repository.getServers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 为每个 ServerData 创建对应的 ServerUiState
    private val _serverUiStates = mutableStateMapOf<UUID, ServerUiState>()
    val serverUiStates: Map<UUID, ServerUiState> = _serverUiStates

    /**
     * 添加新服务器
     */
    fun addServer(serverData: ServerData) {
        viewModelScope.launch {
            repository.addServer(serverData)
        }
        addServerUiState(serverData)
    }

    private fun addServerUiState(serverData: ServerData) {
        viewModelScope.launch {
            _serverUiStates[serverData.id] = serverData.getServerUiState()
        }
    }

    /**
     * 删除服务器
     */
    fun deleteServer(serverId: UUID) {
        viewModelScope.launch {
            repository.deleteServer(serverId)
        }
        deleteServerUiState(serverId)
    }

    private fun deleteServerUiState(serverId: UUID) {
        viewModelScope.launch {
            _serverUiStates.remove(serverId)
        }
    }

    fun updateServerState(serverId: UUID) {
        viewModelScope.launch {
            updateServerUiState(serverId)
            val serverData = servers.value.find { it.id == serverId }
            serverData?.updateStatue()
            updateServerUiState(serverId)
        }
    }

    fun updateServersStatus() {
        viewModelScope.launch {
            updateServersUiStatus()
            servers.value.map { server ->
                launch {
                    server.updateStatue()
                    updateServerUiState(server.id)
                }
            }
        }
    }

    fun updateServersUiStatus() {
        viewModelScope.launch {
            servers.collect { serverList ->
                serverList.forEach { serverData ->
                    updateServerUiState(serverData.id)
                }
            }
        }
    }

    fun updateServerUiState(serverId: UUID) {
        viewModelScope.launch {
            val serverData = servers.value.find { it.id == serverId }
            serverData?.let {
                val server = _serverUiStates[serverId]
                if (server == null) {
                    addServerUiState(it)
                } else {
                    server.name.value = it.name
                    server.host.value = it.host
                    server.port.value = it.port
                    server.version.value = it.version
                    server.protocol.value = it.protocol
                    server.icon.value = it.icon
                    server.playersMax.value = it.playersMax
                    server.playersOnline.value = it.playersOnline
                    server.players.value = it.playersList
                    server.description.value = it.description
                    server.isOnline.value = it.isOnline
                    server.latency.value = it.latency
                    server.lastChecked.value = it.lastChecked
                    server.modLoader.value = it.modLoader
                    server.isGettingStatue.value = it.isGettingStatue
                }
            }
        }
    }
}