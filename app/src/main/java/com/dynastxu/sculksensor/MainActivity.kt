package com.dynastxu.sculksensor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dynastxu.sculksensor.screens.AddServerScreen
import com.dynastxu.sculksensor.screens.MessageScreen
import com.dynastxu.sculksensor.screens.ProfileScreen
import com.dynastxu.sculksensor.screens.ServersScreen
import com.dynastxu.sculksensor.ui.theme.SculkSensorTheme

const val ROUTE_SERVERS = "servers"
const val ROUTE_MESSAGE = "message"
const val ROUTE_PROFILE = "profile"
const val ROUTE_ADD_SERVER = "add_server"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SculkSensorTheme {
                MainApp()
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Preview(showSystemUi = true)
@Composable
fun MainApp() {
    // 1. 创建导航控制器，它是所有导航操作的核心
    val navController = rememberNavController()

    // 2. 观察返回栈状态，获取当前路由
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 3. 定义底部导航的三个项目
    val bottomNavItems = listOf(
        BottomNavItem(title = stringResource(R.string.title_servers), icon = Icons.AutoMirrored.Filled.List, route = ROUTE_SERVERS),
        BottomNavItem(title = stringResource(R.string.title_message), icon = Icons.AutoMirrored.Filled.Message, route = ROUTE_MESSAGE),
        BottomNavItem(title = stringResource(R.string.title_person), icon = Icons.Default.Person, route = ROUTE_PROFILE)
    )

    // 4. 判断是否显示返回键
    val showBackButton = (currentRoute != ROUTE_SERVERS) and (currentRoute != ROUTE_MESSAGE) and (currentRoute != ROUTE_PROFILE)

    Scaffold(
        topBar = {
            // 顶部栏
            AppTopBar(
                title = when (currentRoute) { // 根据路由设置标题
                    ROUTE_SERVERS -> stringResource(R.string.title_servers)
                    ROUTE_MESSAGE -> stringResource(R.string.title_message)
                    ROUTE_PROFILE -> stringResource(R.string.title_person)
                    ROUTE_ADD_SERVER -> stringResource(R.string.title_add_server)
                    else -> stringResource(R.string.app_name)
                },
                showBackButton = showBackButton,
                onBackClick = { navController.navigateUp() } // 点击返回键时弹出返回栈
            )
        },
        bottomBar = {
            // 底部导航栏
            // 仅在三个主页面显示底部栏（简单示例，可根据需要调整）
            if (currentRoute in listOf(ROUTE_SERVERS, ROUTE_MESSAGE, ROUTE_PROFILE)) {
                BottomNavigationBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            // 重要的导航选项：避免重复点击创建多个实例
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { innerPadding -> // innerPadding是Scaffold计算出的内边距，避免内容被栏遮挡
        // 页面导航容器
        // 导航图：定义路线和页面的映射关系
        NavHost(
            navController = navController,
            startDestination = ROUTE_SERVERS, // 起始页
            modifier = Modifier.padding(innerPadding)
        ) {
            // 将路由与前面定义的页面组件关联起来
            composable(ROUTE_SERVERS) { ServersScreen(navController) }
            composable(ROUTE_MESSAGE) { MessageScreen() }
            composable(ROUTE_PROFILE) { ProfileScreen() }
            composable(ROUTE_ADD_SERVER) { AddServerScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onItemClick: (String) -> Unit // 点击事件的回调
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                // 当前项是否被选中（高亮）
                selected = currentRoute == item.route,
                onClick = { onItemClick(item.route) }, // 点击时通知父组件
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBackButton: Boolean,
    onBackClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            }
        },
        actions = {
            // 更多选项按钮（可以后续扩展菜单）
            IconButton(onClick = { /* 暂不处理 */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "更多")
            }
        }
    )
}