package com.example.ainotes.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.ainotes.mvi.search.ChatViewModel
import com.example.ainotes.presentation.components.ChatMessageItem
import com.example.ainotes.presentation.components.ModelSelectionOverlay
import com.example.linguareader.R
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun ChatScreen(
    navController: NavHostController,
    chatViewModel: ChatViewModel
) {
    var userInput by rememberSaveable { mutableStateOf("") }
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    var showModelPanel by rememberSaveable { mutableStateOf(false) }

    val chatMessages by chatViewModel.chatMessages.collectAsState()
    val selectedModel by chatViewModel.selectedModel.collectAsState()
    val models = chatViewModel.availableModels

    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(chatMessages) {
        coroutineScope.launch {
            listState.animateScrollToItem(chatMessages.size)
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Чат",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = selectedModel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Сообщения
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(chatMessages) { message ->
                        ChatMessageItem(
                            message = message,
                            onCreateNote = { selectedText ->
                                val encoded = URLEncoder.encode(selectedText, "UTF-8")
                                // noteId = -1 означает «новая»
                                navController.navigate("add_edit_note/-1?text=$encoded")
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Поле ввода
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    TextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .animateContentSize()
                            .heightIn(min = 56.dp, max = 300.dp)
                            .wrapContentHeight(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.message),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.LightGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_chat),
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                        },
                        trailingIcon = {
                            if (userInput.isNotBlank()) {
                                IconButton(onClick = {
                                    chatViewModel.sendMessage(userInput)
                                    userInput = ""
                                    keyboardController?.hide()
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_send_message),
                                        contentDescription = "Отправить сообщение",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.LightGray
                                    )
                                }
                            }
                        },
                        singleLine = false,
                        maxLines = 10,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            disabledContainerColor = Color.LightGray,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = {
                            if (userInput.isNotBlank()) {
                                chatViewModel.sendMessage(userInput)
                                userInput = ""
                                keyboardController?.hide()
                            }
                        })
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showModelPanel = true },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            tint = Color.LightGray,
                            contentDescription = "Выбор модели"
                        )
                    }
                }
            }

            // Панель выбора модели
            if (showModelPanel) {
                ModelSelectionOverlay(
                    models = models,
                    selectedModel = selectedModel,
                    onModelSelected = {
                        chatViewModel.setModel(it)
                        showModelPanel = false
                    },
                    onDismiss = { showModelPanel = false }
                )
            }
        }
    }
}