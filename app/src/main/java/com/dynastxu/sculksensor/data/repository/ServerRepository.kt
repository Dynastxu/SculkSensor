package com.dynastxu.sculksensor.data.repository

import android.content.Context
import com.dynastxu.sculksensor.data.datastore.AppDataStore
import com.dynastxu.sculksensor.data.model.ServerData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

class ServerRepository(private val context: Context) {
    private val gson = Gson()

    /**
     * 获取所有服务器
     */
    fun getServers(): Flow<List<ServerData>> {
        return AppDataStore.getServerList(context).map { json ->
            if (json.isNullOrBlank()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<ServerData>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            }
        }
    }

    /**
     * 添加新服务器
     */
    suspend fun addServer(serverData: ServerData) {
        val currentList = getCurrentList()
        val newList = currentList.toMutableList().apply { add(serverData) }
        saveServerList(newList)
    }

    /**
     * 删除服务器
     */
    suspend fun deleteServer(serverId: UUID) {
        val currentList = getCurrentList()
        val newList = currentList.filter { it.id != serverId }
        saveServerList(newList)
    }

    /**
     * 更新服务器
     * @param serverData 要更新的服务器数据
     */
    suspend fun updateServer(serverData: ServerData) {
        val currentList = getCurrentList()
        val newList = currentList.map { if (it.id == serverData.id) serverData else it }
        saveServerList(newList)
    }

    suspend fun saveServersStatues(serversDatas: List<ServerData>) {
        val currentList = getCurrentList()
        val newList = serversDatas.map { serverData ->
            val currentServer = currentList.find { it.id == serverData.id }
            currentServer ?: serverData
        }
        saveServerList(newList)
    }

    // 私有辅助方法
    private suspend fun getCurrentList(): List<ServerData> {
        val json = AppDataStore.getServerList(context).first() // 注意：这里需要协程支持
        return if (json.isNullOrBlank()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<ServerData>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    private suspend fun saveServerList(serverData: List<ServerData>) {
        val json = gson.toJson(serverData)
        AppDataStore.saveServerList(context, json)
    }
}