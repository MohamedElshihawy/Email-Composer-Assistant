package com.example.gptprompttask.assistantBot

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BotViewModel(private val botRequest: BotRequest) : ViewModel() {

    private val _lastSendMessage = mutableStateOf("")
    val lastSendMessage: State<String> = _lastSendMessage

    val myMessages = mutableStateListOf<String>()

    private val _lastResponse = mutableStateOf("")
    val lastResponse: State<String> = _lastResponse

    val allResponses = mutableStateListOf<String>()

    fun sendMessage(message: String) {
        _lastSendMessage.value = message
        myMessages.add(message)
        viewModelScope.launch(Dispatchers.IO) {
            botRequest.chatWithBot(message).collect { response ->
                withContext(Dispatchers.Main) {
                    _lastResponse.value = response
                    allResponses.add(response)
                }
            }
        }
    }
}
