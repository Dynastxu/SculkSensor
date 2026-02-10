package com.dynastxu.sculksensor.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dynastxu.sculksensor.R
import com.dynastxu.sculksensor.TAG_SERVER_DETAILS_SCREEN_RENDERING
import com.dynastxu.sculksensor.data.model.PlayerData
import com.dynastxu.sculksensor.data.model.ServerUiState
import com.dynastxu.sculksensor.viewmodel.ServerViewModel

@Composable
fun ServerDetailsScreen(navController: NavController, viewModel: ServerViewModel) {
    val server = viewModel.getSelectedServer()
    if (server == null) {
        Log.e(TAG_SERVER_DETAILS_SCREEN_RENDERING, "未选择服务器")
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), // 添加内边距
        horizontalAlignment = Alignment.CenterHorizontally // 水平居中
    ) {
        ServerListItem(
            serverId = server.id,
            viewModel = viewModel,
            navController = navController,
            clickable = false,
            clipImage = false,
            showDescription = true
        )
        PlayerList(server.playersList, server.playersOnline)
        ServerDetailsForm(viewModel.getServerUiState(server.id))
    }
}

@Composable
fun PlayerList(playerList: List<PlayerData>?, playerOnline: Int) {
    if (playerList == null) return
    if (playerOnline == 0) return
    val anonymousPlayerNum = playerOnline - playerList.size
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.text_player_list),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            if (!playerList.isEmpty()) {
                playerList.forEach {
                    PlayerListItem(it)
                }
            }
            Spacer(Modifier.height(4.dp))
            if (anonymousPlayerNum > 0) {
                Text(stringResource(R.string.text_anonymous_players) + " $anonymousPlayerNum")
            }
        }
    }
}

@Composable
fun PlayerListItem(playerData: PlayerData) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start // 让内容居左
    ) {
        Spacer(Modifier.height(4.dp))
        Text(playerData.name)
    }
}

@Composable
fun ServerDetailsForm(serverUiState: ServerUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 表格标题
            Text(
                text = stringResource(R.string.text_server_details),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 表格内容
            DetailRow(label = stringResource(R.string.label_name), value = serverUiState.name.value)
            DetailRow(label = stringResource(R.string.label_host), value = serverUiState.host.value)
            DetailRow(
                label = stringResource(R.string.label_port),
                value = serverUiState.port.value.toString()
            )
            DetailRow(
                label = stringResource(R.string.label_version),
                value = serverUiState.version.value
            )
            DetailRow(
                label = stringResource(R.string.label_protocol),
                value = serverUiState.protocol.value.toString()
            )
            DetailRow(
                label = stringResource(R.string.label_player_max),
                value = serverUiState.playersMax.value.toString()
            )
            DetailRow(
                label = stringResource(R.string.label_player_online),
                value = serverUiState.playersOnline.value.toString()
            )
            DetailRow(
                label = stringResource(R.string.label_description),
                value = if (serverUiState.description.value.trim().length <= 30) serverUiState.description.value.trim() else "${
                    serverUiState.description.value.trim().substring(
                        0,
                        29
                    )
                }..."
            )
            DetailRow(
                label = stringResource(R.string.label_is_online),
                value = stringResource(if (serverUiState.isOnline.value) R.string._true_ else R.string._false_)
            )
            DetailRow(
                label = stringResource(R.string.label_latency),
                value = "${serverUiState.latency.value} ms"
            )
            serverUiState.lastChecked.value?.let {
                DetailRow(
                    label = stringResource(R.string.label_last_checked),
                    value = java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date(it))
                )
            }
            serverUiState.modLoader.value?.let {
                DetailRow(
                    label = stringResource(R.string.label_mod_loader),
                    value = stringResource(serverUiState.modLoader.value!!)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
