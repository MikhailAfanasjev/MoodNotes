package com.example.ainotes.ViewModels.chat

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainotes.chatGPT.ChatGPTApiService
import com.example.ainotes.chatGPT.ChatGPTRequest
import com.example.ainotes.chatGPT.Message
import com.example.ainotes.data.local.entity.ChatMessageEntity
import com.example.ainotes.data.repository.ChatMessageRepository
import com.example.ainotes.utils.cleanResponse
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.BufferedSource
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val api: ChatGPTApiService,
    private val chatRepo: ChatMessageRepository
) : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<Message>>(emptyList())
    val chatMessages: StateFlow<List<Message>> = _chatMessages.asStateFlow()

    private val _selectedModel = MutableStateFlow("grok-3-gemma3-12b-distilled")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _systemPrompt = MutableStateFlow("Пиши ответы на русском языке")

    val availableModels = listOf(
        "gemma-3-1b-it",
        "gemma-3-4b-it",
        "grok-3-gemma3-12b-distilled",
        "gemma-3-27b-it"
    )

    init {
        viewModelScope.launch {
            // Загружаем из БД только пользовательские и ассистентские сообщения
            val persisted = chatRepo.getAllMessages()
                .map { Message(it.role, it.content) }
            _chatMessages.value = persisted
        }
    }

    fun setSystemPrompt(prompt: String) {
        _systemPrompt.value = prompt
    }

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    private fun addMessage(message: Message) {
        _chatMessages.value = _chatMessages.value + message
        viewModelScope.launch {
            chatRepo.addMessage(
                ChatMessageEntity(
                    role = message.role,
                    content = message.content,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    private fun updateLastAssistantMessage(content: String) {
        val messages = _chatMessages.value.toMutableList()
        val idx = messages.indexOfLast { it.role == "assistant" }
        if (idx != -1) {
            messages[idx] = messages[idx].copy(content = content)
            _chatMessages.value = messages
        }
    }

    fun sendMessage(inputText: String) {
        // Добавляем user-сообщение в UI и репозиторий
        addMessage(Message(role = "user", content = inputText))

        viewModelScope.launch(Dispatchers.IO) {
            // Строим список сообщений для API: сначала системный, затем все UI-сообщения
            val allMessages = listOf(Message("system", _systemPrompt.value)) + _chatMessages.value

            val request = ChatGPTRequest(
                model = _selectedModel.value,
                messages = allMessages,
                stream = true
            )

            try {
                val response = api.sendChatMessage(request)
                if (response.isSuccessful) {
                    response.body()?.source()?.let { source ->
                        streamResponse(source)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        addMessage(Message(role = "assistant", content = "Ошибка: ${response.code()}"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addMessage(Message(role = "assistant", content = "Exception: ${e.localizedMessage}"))
                }
            }
        }
    }

    private suspend fun streamResponse(source: BufferedSource) {
        val gson = Gson()
        val builder = StringBuilder()

        // 1) Добавляем пустое assistant-сообщение в UI
        withContext(Dispatchers.Main) {
            addMessage(Message(role = "assistant", content = ""))
        }

        // 2) Стримим чанки и обновляем UI
        while (!source.exhausted()) {
            val line = source.readUtf8Line().orEmpty()
            if (line.trim() == "data: [DONE]") break

            if (line.startsWith("data:")) {
                val jsonLine = line.removePrefix("data:").trim()
                val chunk = runCatching {
                    gson.fromJson(jsonLine, JsonObject::class.java)
                        .getAsJsonArray("choices")[0]
                        .asJsonObject["delta"].asJsonObject
                        .get("content")?.asString.orEmpty()
                }.getOrNull().orEmpty()

                if (chunk.isNotEmpty()) {
                    builder.append(chunk)
                    val cleaned = cleanResponse(builder.toString()).toString()
                    withContext(Dispatchers.Main) {
                        updateLastAssistantMessage(cleaned)
                    }
                }
            }
        }

        val finalRaw = builder.toString()
        val finalCleaned = cleanResponse(finalRaw).toString()

        withContext(Dispatchers.Main) {
            updateLastAssistantMessage(finalCleaned)
        }

        // Сохраняем сырое в БД
        chatRepo.addMessage(
            ChatMessageEntity(
                role = "assistant",
                content = finalRaw,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        viewModelScope.launch {
            chatRepo.deleteAllMessages()
        }
    }
}
