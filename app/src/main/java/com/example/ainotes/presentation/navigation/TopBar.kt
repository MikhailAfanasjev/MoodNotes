package com.example.ainotes.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ainotes.ViewModels.chat.ChatViewModel
import com.example.ainotes.ViewModels.notes.NotesViewModel
import com.example.ainotes.chatGPT.Message
import com.example.ainotes.presentation.ui.theme.Blue
import com.example.ainotes.presentation.ui.theme.LightGray
import com.example.ainotes.presentation.ui.theme.UltraLightGray
import com.example.ainotes.presentation.ui.theme.White
import com.example.ainotes.utils.NoRippleTheme
import com.example.linguareader.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    chatViewModel: ChatViewModel = hiltViewModel(),
    chatMessages: List<Message>,
    notesViewModel: NotesViewModel = hiltViewModel()
) {
    val iconSize = 24.dp
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/") ?: ""
    val notes by notesViewModel.notes.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showModelMenu by remember { mutableStateOf(false) }
    val selectedModel by chatViewModel.selectedModel.collectAsState()
    val models = chatViewModel.availableModels
    // Для позиционирования меню модели
    var menuBounds by remember { mutableStateOf<Rect?>(null) }
    var modelItemBounds by remember { mutableStateOf<Rect?>(null) }
    var columnBounds by remember { mutableStateOf<Rect?>(null) }
    // Получаем ширину экрана в dp
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    // Задаём расстояние между «Чат» и «Заметки» как, скажем, 8% от ширины экрана
    val dynamicSpacing = screenWidthDp * 0.2f
    LocalDensity.current

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .onGloballyPositioned { coords ->
                    columnBounds = coords.boundsInWindow()
                }
        ) {
            TopAppBar(
                title = { /* пусто */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        drawContent()
                        val stroke = 2.dp.toPx()
                        drawLine(
                            color = LightGray,
                            start = Offset(0f, size.height - stroke / 2),
                            end = Offset(size.width, size.height - stroke / 2),
                            strokeWidth = stroke
                        )
                    },
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // --- Иконка приложения ---
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 8.dp)
                        )

                        // Chat button + label
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { navController.navigate("chat") }
                                .padding(end = 12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chat),
                                contentDescription = "Чат",
                                tint = if (currentRoute == "chat") Blue else LightGray,
                                modifier = Modifier.size(iconSize)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Чат",
                                color = if (currentRoute == "chat") Blue else LightGray,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(dynamicSpacing))

                        // Notes button + label
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { navController.navigate("notes") }
                                .padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_notes),
                                contentDescription = "Заметки",
                                tint = if (currentRoute == "notes") Blue else LightGray,
                                modifier = Modifier.size(iconSize)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Заметки",
                                color = if (currentRoute == "notes") Blue else LightGray,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(dynamicSpacing))
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Больше",
                                modifier = Modifier.size(iconSize),
                                tint = LightGray
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = {
                                showMenu = false
                                showModelMenu = false
                            },
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    menuBounds = coords.boundsInWindow()
                                }
                                .width(200.dp),
                            shape = RoundedCornerShape(16.dp),       // ← скругление
                            containerColor = Color.White,            // ← фон контейнера
                            tonalElevation = 8.dp                    // ← тень (M3), можно играть значением
                        ) {

                            // Секция Model
                            DropdownMenuItem(
                                modifier = Modifier
                                    // сохраняем координаты — оставляем как есть
                                    .onGloballyPositioned { coords ->
                                        modelItemBounds = coords.boundsInWindow()
                                    }
                                    // добавляем фон, когда showModelMenu == true
                                    .background(if (showModelMenu) UltraLightGray else Color.Transparent),
                                text = { Text("Выбор модели") },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_more),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .rotate(if (showModelMenu) 270f else 0f)
                                            .size(16.dp)
                                    )
                                },
                                onClick = {
                                    showModelMenu = true
                                }
                            )

                            // Остальные пункты меню
                            DropdownMenuItem(
                                text = { Text("Тема") },
                                onClick = {
                                    showMenu = false
                                    showModelMenu = false
                                }
                            )
                            if (currentRoute == "chat" && chatMessages.isNotEmpty()) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_remove),
                                            contentDescription = "Очистить чат",
                                            tint = colorResource(id = R.color.orange),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Очистить чат",
                                            color = colorResource(id = R.color.orange)
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        showModelMenu = false
                                        chatViewModel.clearChat()
                                    }
                                )
                            }
                            if (currentRoute == "notes" && notes.isNotEmpty()) {
                                IconButton(onClick = { notesViewModel.deleteAllNotes() }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_remove),
                                        contentDescription = "Удалить все",
                                        tint = Color.Red,
                                        modifier = Modifier.size(iconSize)
                                    )
                                }
                            }
                        }

                        // Вложенное меню выбора модели
                        if (showModelMenu && menuBounds != null && modelItemBounds != null) {
                            val offsetDp = with(LocalDensity.current) {
                                DpOffset(
                                    x = menuBounds!!.width.toDp(),
                                    y = (modelItemBounds!!.bottom - menuBounds!!.top).toDp()
                                )
                            }
                            DropdownMenu(
                                expanded = true,
                                onDismissRequest = { showModelMenu = false },
                                offset = offsetDp,
                                modifier = Modifier
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(12.dp))      // <-- скругляем
                                    .background(Color.White)              // <-- фон
                            ) {
                                models.forEach { model ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                RadioButton(
                                                    selected = model == selectedModel,
                                                    onClick = {
                                                        chatViewModel.setModel(model)
                                                        showModelMenu = false
                                                    }
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(model)
                                            }
                                        },
                                        onClick = {
                                            chatViewModel.setModel(model)
                                            showModelMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
                colors = topAppBarColors(
                    containerColor = White,
                    navigationIconContentColor = LightGray,
                    actionIconContentColor = LightGray
                )
            )
        }
    }
}