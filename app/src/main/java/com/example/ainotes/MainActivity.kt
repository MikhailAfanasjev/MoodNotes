package com.example.ainotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ainotes.presentation.navigation.TopBar
import com.example.ainotes.presentation.navigation.NavGraph
import com.example.ainotes.chatGPT.ApiKeyHelper
import com.example.ainotes.ViewModels.chat.ChatViewModel
import com.example.ainotes.ViewModels.notes.NotesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiKeyHelper.init(this)

        setContent {
            MaterialTheme {
                // обновляем системные панели
                DisposableEffect(Unit) {
                    updateSystemBars()
                    onDispose { }
                }

                // 1) NavController
                val navController = rememberNavController()
                // 2) Получаем единственный экземпляр ChatViewModel как Composable‑VM
                val chatViewModel: ChatViewModel = hiltViewModel()
                // 3) Подписываемся на список сообщений
                val chatMessages by chatViewModel.chatMessages.collectAsState()
                val notesViewModel: NotesViewModel = hiltViewModel()
                val notes by notesViewModel.notes.collectAsState()

                Scaffold(
                    topBar = {
                        val currentRoute = navController
                            .currentBackStackEntryAsState()
                            .value
                            ?.destination
                            ?.route ?: ""

                        if (!currentRoute.startsWith("detail")) {
                            TopBar(
                                navController     = navController,
                                chatViewModel     = chatViewModel,
                                chatMessages      = chatMessages,
                                notesViewModel    = notesViewModel
                            )
                        }
                    }
                ) { innerPadding ->
                    // Прокидываем chatViewModel дальше в NavGraph
                    NavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        chatViewModel = chatViewModel,
                        notesViewModel = notesViewModel
                    )
                }
            }
        }
    }

    private fun updateSystemBars() {
        window.statusBarColor = Color.White.toArgb()
        WindowCompat.getInsetsController(window, window.decorView)?.apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }
}