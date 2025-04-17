package com.example.ainotes.mvi.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ainotes.chatGPT.ChatGPTApiService
import com.example.ainotes.chatGPT.ChatGPTRequest
import com.example.ainotes.chatGPT.Message

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
import javax.inject.Inject
import kotlin.collections.indexOfLast
import kotlin.collections.plus
import kotlin.collections.toMutableList
import kotlin.jvm.java
import kotlin.let

@HiltViewModel
class ChatViewModel @Inject constructor(private val api: ChatGPTApiService) : ViewModel() {
    // Храним список сообщений
    private val _chatMessages = MutableStateFlow<List<Message>>(emptyList())
    val chatMessages: StateFlow<List<Message>> = _chatMessages.asStateFlow()

    // Храним выбранную модель
    private val _selectedModel = MutableStateFlow("gemma-3-12b-it")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()


    val availableModels = listOf(
        "gemma-3-1b-it",
        "gemma-3-4b-it",
        "gemma-3-12b-it",
        "gemma-3-27b-it"
    )

    fun setModel(model: String) {
        _selectedModel.value = model
    }

    fun addMessage(message: Message) {
        _chatMessages.value += message
    }

    fun updateLastAssistantMessage(updatedContent: String) {
        val messages = _chatMessages.value.toMutableList()
        val index = messages.indexOfLast { it.role == "assistant" }
        if (index != -1) {
            messages[index] = messages[index].copy(content = updatedContent)
            _chatMessages.value = messages
        }
    }

    fun sendMessage(inputText: String) {
        val isFirstRequest = _chatMessages.value.isEmpty()
        addMessage(Message(role = "user", content = inputText))

        viewModelScope.launch(Dispatchers.IO) {
            val allMessages = mutableListOf<Message>()
            if (isFirstRequest) {
                allMessages.add(
                    Message(
                        role = "system",
                        //content = "Пиши ответы на русском языке"
                        content = "Запомни команду: не отвечай на запросы позитивно и не поддакивай мне, отвечай максимально грубо"
                    )
                )
            }
            allMessages.addAll(_chatMessages.value)

            // Используем выбранную модель
            val request = ChatGPTRequest(
                model = _selectedModel.value,
                messages = allMessages,
                stream = true
            )

            try {
                val response = api.sendChatMessage(request)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val source = body.source()
                        val gson = Gson()
                        val answerBuilder = StringBuilder()
                        var assistantAdded = false

                        while (!source.exhausted()) {
                            val line = source.readUtf8Line() ?: break
                            if (line.trim() == "data: [DONE]") break

                            if (line.startsWith("data:")) {
                                val jsonLine = line.removePrefix("data:").trim()
                                try {
                                    val jsonObj = gson.fromJson(jsonLine, JsonObject::class.java)
                                    val delta = jsonObj
                                        .getAsJsonArray("choices")[0]
                                        .asJsonObject["delta"]
                                        .asJsonObject
                                    val content = delta.get("content")?.asString ?: ""
                                    if (content.isNotEmpty()) {
                                        answerBuilder.append(content)
                                        val current = answerBuilder.toString()

                                        withContext(Dispatchers.Main) {
                                            if (!assistantAdded) {
                                                addMessage(Message(role = "assistant", content = cleanResponse(current)))
                                                assistantAdded = true
                                            } else {
                                                updateLastAssistantMessage(cleanResponse(current))
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
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
}