package com.dynastxu.sculksensor.data.model

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import com.dynastxu.sculksensor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.UUID

/**
 * 用于存储服务器信息
 * @param id 自动生成的 UUID
 * @param name 名称（必需）
 * @param host 地址（必需）
 * @param port 端口（必需）
 * @param version 版本
 * @param protocol 协议
 * @param icon 图标（ Base64 格式的字符串）
 * @param playersMax 最大玩家数
 * @param playersOnline 在线玩家数
 * @param playersList 在线玩家列表
 * @param description 描述
 * @param isOnline 是否在线
 * @param latency 延迟
 * @param lastChecked 最后一次检查时间
 * @param modLoader Mod 加载器
 */
@Serializable
data class ServerData(
    val id: UUID = UUID.randomUUID(),
    var name: String,
    var host: String,
    var port: Int = 25565,
    var version: String = "",
    var protocol: Int = 0,
    var icon: String = "",
    var playersMax: Int = 0,
    var playersOnline: Int = 0,
    var playersList: List<PlayerData> = emptyList(),
    var description: String = "",
    var isOnline: Boolean = false,
    var latency: Long = -1,
    var lastChecked: Long? = null,
    var modLoader: Int? = null
) {
    @Transient
    private val TAG_GET_SERVER_STATUE = "服务器状态查询"
    @Transient
    private var isGettingStatue: Boolean = false

    /**
     * 获取服务器状态
     */
    suspend fun updateStatue() {
        withContext(Dispatchers.IO) {
            try {
                if (isGettingStatue) {
                    Log.w(TAG_GET_SERVER_STATUE, "重复执行获取服务器状态")
                    return@withContext
                }
                Log.d(TAG_GET_SERVER_STATUE, "开始获取服务器 $host 状态")
                isGettingStatue = true
                Socket(host, port).use { socket ->
                    DataOutputStream(socket.getOutputStream()).use { out ->
                        DataInputStream(socket.getInputStream()).use { `in` ->

                            // 1. 发送握手包
                            sendHandshake(out, host, port)

                            // 2. 发送状态请求包
                            sendStatusRequest(out)

                            // 3. 接收状态响应
                            val jsonResponse = receiveStatusResponse(`in`)
                            Log.i(TAG_GET_SERVER_STATUE, "服务器 $host 响应的 json ：$jsonResponse")
                            try {
                                val map = parseServerResponse(jsonResponse)
                                isOnline = true
                                lastChecked = System.currentTimeMillis()

                                // 安全解析版本和协议
                                version = map["version"]?.jsonObject?.get("name")?.jsonPrimitive?.content
                                    ?: "Unknown"
                                protocol = map["version"]?.jsonObject?.get("protocol")?.jsonPrimitive?.int ?: 0

                                // 安全解析图标
                                icon = map["favicon"]?.jsonPrimitive?.content ?: ""

                                // 安全解析玩家信息
                                playersMax = map["players"]?.jsonObject?.get("max")?.jsonPrimitive?.int ?: 0
                                playersOnline = map["players"]?.jsonObject?.get("online")?.jsonPrimitive?.int ?: 0

                                // 安全解析玩家列表
                                playersList = map["players"]?.jsonObject?.get("sample")?.jsonArray?.mapNotNull {
                                    try {
                                        PlayerData(
                                            name = it.jsonObject["name"]?.jsonPrimitive?.content ?: "",
                                            id = it.jsonObject["id"]?.jsonPrimitive?.content ?: "",
                                            isOnline = true,
                                            lastChecked = lastChecked
                                        )
                                    } catch (e: Exception) {
                                        Log.e(TAG_GET_SERVER_STATUE, "解析服务器 $host 玩家列表失败： $e")
                                        null
                                    }
                                } ?: emptyList()

                                // 安全解析描述（可能为字符串或对象）
                                description = when (val desc = map["description"]) {
                                    is JsonPrimitive -> desc.content
                                    is JsonObject -> desc["text"]?.jsonPrimitive?.content ?: desc.toString()
                                    else -> ""
                                }

                                if (map.contains("forgeData")) {
                                    modLoader = R.string.modloader_forge
                                }
                            } catch (e: Exception) {
                                Log.e(TAG_GET_SERVER_STATUE, "解析服务器 $host 响应失败: ${e.message}")
                                // 可设置默认值或抛出错误
                                isOnline = false
                                version = "Unknown"
                                protocol = 0
                                playersOnline = 0
                                playersMax = 0
                                playersList = emptyList()
                                description = "Failed to parse response"
                            }
                            // 发送 Ping 请求计算延迟
                            latency = measureLatency(out, `in`)
                            Log.i(TAG_GET_SERVER_STATUE, "服务器 $host 延迟： $latency ms")
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG_GET_SERVER_STATUE, "获取服务器 $host 状态失败： $e")
                setOffline()
            } finally {
                isGettingStatue = false
            }
        }
    }

    /**
     * 获取用于 ui 绑定的数据
     */
    fun getServerUiState() : ServerUiState {
        return ServerUiState(
            name = mutableStateOf(name),
            host = mutableStateOf(host),
            port = mutableIntStateOf(port),
            version = mutableStateOf(version),
            protocol = mutableIntStateOf(protocol),
            icon = mutableStateOf(icon),
            playersMax = mutableIntStateOf(playersMax),
            playersOnline = mutableIntStateOf(playersOnline),
            players = mutableStateOf(playersList),
            description = mutableStateOf(description),
            isOnline = mutableStateOf(isOnline),
            latency = mutableLongStateOf(latency),
            lastChecked = mutableStateOf(lastChecked),
            modLoader = mutableStateOf(modLoader)
        )
    }

    private fun setOffline() {
        isOnline = false
        playersOnline = 0
        playersList = emptyList()
        latency = -1
        lastChecked = System.currentTimeMillis()
    }

    /**
     * 发送握手包
     */
    @Throws(IOException::class)
    private fun sendHandshake(out: DataOutputStream, host: String, port: Int) {
        val handshakeBuffer = ByteArrayOutputStream()
        val handshake = DataOutputStream(handshakeBuffer)

        handshake.writeByte(0x00) // 包ID
        writeVarInt(handshake, -1) // 协议版本（-1 表示未知）
        writeString(handshake, host) // 服务器地址
        handshake.writeShort(port) // 端口
        writeVarInt(handshake, 1) // 下一状态：1 为状态查询

        // 写入包长度 + 数据
        out.writeByte(handshakeBuffer.size()) // 包长度
        out.write(handshakeBuffer.toByteArray())
        out.flush()
    }

    /**
     * 发送状态请求包
     */
    @Throws(IOException::class)
    private fun sendStatusRequest(out: DataOutputStream) {
        out.writeByte(0x01) // 包长度
        out.writeByte(0x00) // 包ID
        out.flush()
    }

    /**
     * 接收状态响应
     */
    @Throws(IOException::class)
    private fun receiveStatusResponse(`in`: DataInputStream): String {
        readVarInt(`in`)
        val packetId = readVarInt(`in`)
        if (packetId != 0x00) {
            throw IOException("无效的包ID：$packetId")
        }
        val jsonLength = readVarInt(`in`)
        val jsonBytes = ByteArray(jsonLength)
        `in`.readFully(jsonBytes)
        return String(jsonBytes, StandardCharsets.UTF_8)
    }

    /**
     * 获取延迟
     */
    @Throws(IOException::class)
    private fun measureLatency(out: DataOutputStream, `in`: DataInputStream): Long {
        val payload = System.currentTimeMillis()
        val startTime = System.nanoTime()

        // 创建Ping请求包
        val pingBuffer = ByteArrayOutputStream()
        DataOutputStream(pingBuffer).use { pingData ->
            // 写入包ID (0x01)
            writeVarInt(pingData, 0x01)
            // 写入Payload
            pingData.writeLong(payload)
        }

        // 发送 Ping 请求: 先写包长度，再写包内容
        writeVarInt(out, pingBuffer.size())
        out.write(pingBuffer.toByteArray())
        out.flush()

        // 接收 Pong 响应
        readVarInt(`in`)  // 读取包长度
        val packetId = readVarInt(`in`)      // 读取包ID

        if (packetId != 0x01) {
            throw IOException("期望的 Pong 包 ID 0x01, 但收到: $packetId")
        }

        // 读取 Payload
        val receivedPayload = `in`.readLong()
        val endTime = System.nanoTime()

        // 验证 Payload (可选)
        if (receivedPayload != payload) {
            Log.w(TAG_GET_SERVER_STATUE,"警告: Ping/Pong Payload 不匹配 (发送: $payload, 接收: $receivedPayload)")
        }

        // 计算延迟 (纳秒转毫秒)
        return (endTime - startTime) / 1_000_000L
    }

    /**
     * 写入 VarInt
     */
    @Throws(IOException::class)
    private fun writeVarInt(out: DataOutputStream, value: Int) {
        var value = value
        while (true) {
            if ((value and 0x7F.inv()) == 0) {
                out.writeByte(value)
                return
            }
            out.writeByte((value and 0x7F) or 0x80)
            value = value ushr 7
        }
    }

    /**
     * 读取 VarInt
     */
    @Throws(IOException::class)
    private fun readVarInt(`in`: DataInputStream): Int {
        var value = 0
        var length = 0
        var currentByte: Byte
        while (true) {
            currentByte = `in`.readByte()
            value = value or ((currentByte.toInt() and 0x7F) shl (length * 7))
            length++
            if (length > 5) throw IOException("VarInt 过长")
            if ((currentByte.toInt() and 0x80) != 0x80) break
        }
        return value
    }

    /**
     * 写入字符串
     */
    @Throws(IOException::class)
    private fun writeString(out: DataOutputStream, str: String) {
        val bytes = str.toByteArray(StandardCharsets.UTF_8)
        writeVarInt(out, bytes.size)
        out.write(bytes)
    }

    private fun parseServerResponse(jsonString: String): Map<String, JsonElement> {
        val json = Json { ignoreUnknownKeys = true }
        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        return jsonObject.toMap()
    }
}
