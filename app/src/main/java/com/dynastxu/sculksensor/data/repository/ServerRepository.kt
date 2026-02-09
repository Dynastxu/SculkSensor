package com.dynastxu.sculksensor.data.repository

import android.content.Context
import com.dynastxu.sculksensor.data.datastore.AppDataStore
import com.dynastxu.sculksensor.data.model.Server
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
    fun getServers(): Flow<List<Server>> {
        return AppDataStore.getServerList(context).map { json ->
            if (json.isNullOrBlank()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<Server>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            }
        }
    }

    /**
     * 添加新服务器
     */
    suspend fun addServer(server: Server) {
        val currentList = getCurrentList()
        val newList = currentList.toMutableList().apply { add(server) }
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

    // 私有辅助方法
    private suspend fun getCurrentList(): List<Server> {
        val json = AppDataStore.getServerList(context).first() // 注意：这里需要协程支持
        return if (json.isNullOrBlank()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Server>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
    }

    private suspend fun saveServerList(servers: List<Server>) {
        val json = gson.toJson(servers)
        AppDataStore.saveServerList(context, json)
    }
}