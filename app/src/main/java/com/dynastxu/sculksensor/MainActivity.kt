package com.dynastxu.sculksensor

import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dynastxu.sculksensor.data.repository.ServerRepository
import com.dynastxu.sculksensor.screens.AddServerScreen
import com.dynastxu.sculksensor.screens.MessageScreen
import com.dynastxu.sculksensor.screens.ProfileScreen
import com.dynastxu.sculksensor.screens.ServerDetailsScreen
import com.dynastxu.sculksensor.screens.ServersScreen
import com.dynastxu.sculksensor.ui.theme.SculkSensorTheme
import com.dynastxu.sculksensor.viewmodel.ServerViewModel

const val ROUTE_SERVERS = "servers"
const val ROUTE_MESSAGE = "message"
const val ROUTE_PROFILE = "profile"
const val ROUTE_ADD_SERVER = "add_server"
const val ROUTE_SERVER_DETAILS = "server_details"

const val TAG_SERVERS_SCREEN_RENDERING = "服务器列表页面渲染"
const val TAG_GET_SERVER_STATUE = "服务器状态查询"
const val TAG_SERVER_VIEW_MODEL = "ServerViewModel"
const val TAG_SERVER_DETAILS_SCREEN_RENDERING = "服务器详情页面渲染"

class MainActivity : ComponentActivity() {
    // 创建 Repository 实例
    private val repository by lazy { ServerRepository(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SculkSensorTheme {
                // 使用自定义 ViewModel 工厂
                val viewModel: ServerViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ServerViewModel(repository) as T
                        }
                    }
                )
                MainApp(viewModel)
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun MainApp(viewModel: ServerViewModel) {
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
                    ROUTE_SERVER_DETAILS -> stringResource(R.string.title_server_details)
                    else -> stringResource(R.string.app_name)
                },
                showBackButton = showBackButton,
                onBackClick = { navController.navigateUp() }, // 点击返回键时弹出返回栈
                currentRoute = currentRoute,
                viewModel = viewModel,
                navController = navController
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
                    },
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
            composable(ROUTE_SERVERS) { ServersScreen(navController = navController, viewModel = viewModel) }
            composable(ROUTE_MESSAGE) { MessageScreen() }
            composable(ROUTE_PROFILE) { ProfileScreen() }
            composable(ROUTE_ADD_SERVER) { AddServerScreen(navController = navController, viewModel = viewModel) }
            composable(ROUTE_SERVER_DETAILS) { ServerDetailsScreen(navController = navController, viewModel = viewModel) }
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
    onBackClick: () -> Unit = {},
    currentRoute: String?,
    viewModel: ServerViewModel,
    navController: NavController
) {
    // 控制菜单是否展开的状态
    var expanded by remember { mutableStateOf(false) }
    var showToast by remember { mutableStateOf(false) }
    var showToastText by remember { mutableStateOf("") }
    var showToastDuration by remember { mutableIntStateOf(Toast.LENGTH_SHORT) }

    fun showToast(text: String, duration: Int){
        showToast = true
        showToastText = text
        showToastDuration = duration
    }

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
            // 更多选项按钮
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "更多")
            }
            // 下拉菜单
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false } // 点击外部区域关闭菜单
            ) {
                if (currentRoute == null) return@DropdownMenu
                else if (currentRoute == ROUTE_SERVERS) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_refresh)) },
                        onClick = {
                            viewModel.updateServersStatus()
                            expanded = false
                            showToast("刷新中", Toast.LENGTH_SHORT)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.menu_item_add_server)) },
                        onClick = {
                            navController.navigate(ROUTE_ADD_SERVER)
                            expanded = false
                        }
                    )
                }
            }
        }
    )
    // 使用 LaunchedEffect 处理 Toast 显示
    if (showToast) {
        Toast.makeText(LocalContext.current, showToastText, showToastDuration).show()
        showToast = false // 重置状态
    }
}