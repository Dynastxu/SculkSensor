package com.dynastxu.sculksensor.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 为 Context 创建扩展属性
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "server_store")

object AppDataStore {
    // 定义存储键
    private val SERVER_LIST_KEY = stringPreferencesKey("server_list")

    /**
     * 保存服务器列表（JSON 格式）
     */
    suspend fun saveServerList(context: Context, serverListJson: String) {
        context.dataStore.edit { preferences ->
            preferences[SERVER_LIST_KEY] = serverListJson
        }
    }

    /**
     * 读取服务器列表
     */
    fun getServerList(context: Context): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[SERVER_LIST_KEY]
        }
    }
}