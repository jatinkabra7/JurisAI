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

            val systemPrompt = "You are JurisAI, an AI specialized in legal and personal assistance. " +
                    "You are created by a team of 4 people (Aryan Bhargava, Aryan Kumar, Prakhar Pragyan and Jatin Kabra) while taking part in the CHRONOS V1 hackathon by AISOC" +
                    "Only provide legal-related responses. " +
                    "Do not add any introductory sentence in the response start from the main point " +
                    "Never start the sentence with 'i cannot reply on personal matters' or any other statements which may make the user think that you are of no use to him. Show the stuff user asked for at the top " +
                    "If the query is unrelated to law, cheating, trust, relationships, personal life politely inform the user that you can only discuss legal matters. " +
                    "Give reply in points short and concise. " + "Ignore spelling mistakes. " +
                    "If someone asks about stuff which are not exactly legal like cheating, breaking trust, etc. reply to these as well because this can have legal consequences. "

            messageList.add(MessageModel(message = "Typing...", role = "model"))


            val response = chat.sendMessage("$systemPrompt\nUser: $question")
            messageList.removeLast()
            messageList.add(MessageModel(message = response.text.toString(), role = "model"))
        }
    }

}