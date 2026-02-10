package com.dynastxu.sculksensor.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.magnifier
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
            clipImage = false
        )
        PlayerList(server.playersList, server.playersOnline)
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
    Row (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start // 让内容居左
    ) {
        Spacer(Modifier.height(4.dp))
        Text(playerData.name)
    }
}