package com.example.ainotes.mvi.chat

import android.util.Log
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

    init {
        // При старте VM загружаем из БД
        viewModelScope.launch {
            val persisted = chatRepo.getAllMessages()
            .map { Message(it.role, it.content) }
            _chatMessages.value = persisted
        }
    }

    private val _selectedModel = MutableStateFlow("grok-3-gemma3-12b-distilled")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _systemPrompt = MutableStateFlow("Пиши ответы на русском языке")
    val systemPrompt: StateFlow<String> = _systemPrompt.asStateFlow()
    private var lastSystemPromptUsed: String? = null

    val availableModels = listOf(
        "gemma-3-1b-it",
        "gemma-3-4b-it",
        "grok-3-gemma3-12b-distilled",
        "gemma-3-27b-it"
    )

    fun setSystemPrompt(prompt: String) {
        _systemPrompt.value = prompt
    }

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    private fun addMessage(message: Message) {
        _chatMessages.value = _chatMessages.value + message
        viewModelScope.launch {
            val entity = ChatMessageEntity(
                role = message.role,
                content = message.content,
                timestamp = System.currentTimeMillis()
            )
            chatRepo.addMessage(entity)
        }
    }

    private fun updateLastAssistantMessage(content: AnnotatedString) {
        val messages = _chatMessages.value.toMutableList()
        val idx = messages.indexOfLast { it.role == "assistant" }
        if (idx != -1) {
            messages[idx] = messages[idx].copy(content = content.toString())
            _chatMessages.value = messages
        }
    }

    fun sendMessage(inputText: String) {
        val currentPromptText = _systemPrompt.value
        addMessage(Message(role = "user", content = AnnotatedString(inputText).toString()))

        viewModelScope.launch(Dispatchers.IO) {
            val allMessages = mutableListOf<Message>()
            if (_chatMessages.value.isEmpty() || currentPromptText != lastSystemPromptUsed) {
                allMessages.add(Message(role = "system", content = AnnotatedString(currentPromptText).toString()))
                lastSystemPromptUsed = currentPromptText
            }
            allMessages.addAll(_chatMessages.value)

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
                        addMessage(Message(role = "assistant", content = AnnotatedString("Ошибка: ${response.code()}").toString()))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    addMessage(Message(role = "assistant", content = AnnotatedString("Exception: ${e.localizedMessage}").toString()))
                }
            }
        }
    }

    private suspend fun streamResponse(source: BufferedSource) {
        val gson = Gson()
        val builder = StringBuilder()
        var assistantAdded = false

        while (!source.exhausted()) {
            val line = source.readUtf8Line().orEmpty()
            if (line.trim() == "data: [DONE]") break
            if (line.startsWith("data:")) {
                val jsonLine = line.removePrefix("data:").trim()
                runCatching {
                    val delta = gson.fromJson(jsonLine, JsonObject::class.java)
                        .getAsJsonArray("choices")[0]
                        .asJsonObject["delta"].asJsonObject
                    delta.get("content")?.asString.orEmpty()
                }.getOrNull()?.takeIf { it.isNotEmpty() }?.let { chunk ->
                    builder.append(chunk)
                    val cleaned = cleanResponse(builder.toString())
                    withContext(Dispatchers.Main) {
                        if (!assistantAdded) {
                            addMessage(Message(role = "assistant", content = cleaned.toString()))
                            assistantAdded = true
                        } else {
                            updateLastAssistantMessage(cleaned)
                        }
                    }
                }
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        lastSystemPromptUsed = null
        viewModelScope.launch {
            chatRepo.deleteAllMessages()
            _chatMessages.value = emptyList()
            lastSystemPromptUsed = null
        }
    }
}
