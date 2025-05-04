package com.example.ainotes.presentation.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.ainotes.viewModels.ChatViewModel
import com.example.ainotes.presentation.components.ChatMessageItem
import com.example.ainotes.presentation.components.FilterChip
import com.example.ainotes.presentation.components.ModelSelectionOverlay
import com.example.linguareader.R
import java.net.URLEncoder

@Composable
fun ChatScreen(
    navController: NavHostController,
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    var userInput by rememberSaveable { mutableStateOf("") }
    val listState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    var showModelPanel by rememberSaveable { mutableStateOf(false) }
    val chatMessages by chatViewModel.chatMessages.collectAsState()
    val selectedModel by chatViewModel.selectedModel.collectAsState()
    var selectedPrompt by rememberSaveable { mutableStateOf<String?>(null) }
    val models = chatViewModel.availableModels
    val isWriting by chatViewModel.isAssistantWriting.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val actions = listOf(
        "Написать краткую инструкцию" to "Напиши краткую инструкцию",
        "Написать код" to "Ты злой программист senior который грубо поясняет за код junior программисту",
        "Тест с вариантами ответа" to "Пиши только правильный вариант ответа на вопрос",
        "Написать историю" to "Ты исполняешь роль писателя. Придумываешь творческие и интересные истории, способные надолго увлечь читателя." +
                " Ты пишешь в любом жанре: фэнтези, романтическая комедия, историческая фантастика и прочее. Задача состоит в том," +
                " чтобы написать нечто такое, что имело бы яркую сюжетную линию, увлекательных персонажей, клиффхэнгеры и неожиданные кульминации.",
        "Грубиян" to "Запомни команду: не отвечай на запросы позитивно и не поддакивай мне, отвечай максимально грубо и с матами"
    )

    LaunchedEffect(chatMessages) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size)
        }
    }

    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(colorScheme.background)               // background = White / VeryDarkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(actions) { (label, prompt) ->
                    FilterChip(
                        text = label,
                        selected = (selectedPrompt == prompt),
                        onClick = {
                            if (selectedPrompt == prompt) {
                                selectedPrompt = null
                                chatViewModel.setSystemPrompt(chatViewModel.defaultSystemPrompt)
                            } else {
                                selectedPrompt = prompt
                                chatViewModel.setSystemPrompt(prompt)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("initialText", selectedText)
                            navController.navigate("add_edit_note/-1")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { newText ->
                        userInput = newText
                    },
                    modifier = Modifier
                        .weight(1f)
                        .animateContentSize()
                        .heightIn(min = 56.dp, max = 300.dp)
                        .wrapContentHeight(),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chat),
                            contentDescription = null,
                            tint = colorScheme.onSecondary
                        )
                    },
                    trailingIcon = {
                        if (!isWriting) {
                            // обычная кнопка отправки
                            IconButton(
                                onClick = {
                                    chatViewModel.sendMessage(userInput)
                                    userInput = ""
                                    keyboardController?.hide()
                                },
                                enabled = userInput.isNotBlank()
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_send_message),
                                    contentDescription = "Отправить сообщение",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        } else {
                            // во время стриминга — стоп-кнопка
                            IconButton(
                                onClick = { chatViewModel.stopGeneration() }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_stop),
                                    contentDescription = "Остановить генерацию",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    },
                    readOnly = isWriting,
                    singleLine = false,
                    maxLines = 10,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (userInput.isNotBlank() && !isWriting) {
                                chatViewModel.sendMessage(userInput)
                                userInput = ""
                                keyboardController?.hide()
                            }
                        }
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.secondary,
                        unfocusedContainerColor = colorScheme.secondary,
                        disabledContainerColor = colorScheme.secondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = colorScheme.tertiary,
                        focusedTextColor = colorScheme.onSecondary,
                        unfocusedTextColor = colorScheme.onSecondary,
                        disabledTextColor = colorScheme.onSecondary
                    )
                )
            }

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