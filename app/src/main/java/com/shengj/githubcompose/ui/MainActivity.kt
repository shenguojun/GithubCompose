package com.shengj.githubcompose.ui

// import com.shengj.githubcompose.ui.popular.PopularReposScreen // 不再需要
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // OAuth Intent 处理仍然可能需要，取决于 LoginActivity 如何与 MainActivity 交互
        // handleIntent(intent)
        enableEdgeToEdge()

        // 设置系统栏为浅色模式（深色图标）- 这应该移到 Theme 或 AppNavigation?
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            // isAppearanceLightNavigationBars = true // 底部导航通常有自己的背景色
        }

        setContent {
            // 记住系统UI控制器
            val systemUiController = rememberSystemUiController()

            // 设置状态栏颜色 - 也可以在 Theme 或特定屏幕设置
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.White, // 或 MaterialTheme.colors.primaryVariant
                    darkIcons = true
                )
                 systemUiController.setNavigationBarColor(
                    color = Color.White, // 与底部导航栏颜色匹配
                    darkIcons = true
                 )
            }

            Surface(color = MaterialTheme.colors.background) {
                AppNavigation() // 使用 AppNavigation
            }
        }
    }

    // 保留 onNewIntent 和 handleIntent 如果 LoginActivity 通过 Intent 回调 MainActivity
    // override fun onNewIntent(intent: Intent) {
    //     super.onNewIntent(intent)
    //     handleIntent(intent)
    // }

    // private fun handleIntent(intent: Intent?) {
    //     // ... (之前 LoginActivity 中的 Intent 处理逻辑，如果需要的话)
    // }
}