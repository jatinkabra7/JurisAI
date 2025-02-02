package com.jatinkabra.music.player.jurisai

import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch

class JurisViewModel : ViewModel() {

    val messageList =
        mutableListOf<MessageModel>()



    val model = GenerativeModel(
        modelName = "gemini-1.5-flash-001",
        apiKey = Constants.apiKey
    )

    @RequiresApi(35)
    fun sendMessage(question : String) {
        viewModelScope.launch {
            val chat = model.startChat(
                history = messageList.map {
                    content(it.role){text(it.message)}
                }.toList()
            )




            messageList.add(MessageModel(message = question, role = "user"))

            val systemPrompt = "You are JurisAI, an AI specialized in legal assistance. " +
                    "Only provide legal-related responses. " +
                    "If the query is unrelated to law, politely inform the user that you can only discuss legal matters." +
                    "Give reply in points short and concise" + "ignore spelling mistakes"

            messageList.add(MessageModel(message = "Typing...", role = "model"))


            val response = chat.sendMessage("$systemPrompt\nUser: $question")
            messageList.removeLast()
            messageList.add(MessageModel(message = response.text.toString(), role = "model"))
        }
    }

}