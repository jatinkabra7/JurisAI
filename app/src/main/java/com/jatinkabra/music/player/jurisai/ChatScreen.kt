package com.jatinkabra.music.player.jurisai

import android.annotation.SuppressLint
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jatinkabra.music.player.jurisai.ui.theme.dark_blue
import com.jatinkabra.music.player.jurisai.ui.theme.light_blue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

@RequiresApi(35)
@Composable
fun ChatScreen(modifier: Modifier = Modifier, viewModel: JurisViewModel) {

    var query by remember {
        mutableStateOf("")
    }


    val focusManager = LocalFocusManager.current

    val messageList = viewModel.messageList


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(light_blue, dark_blue)))
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = "JurisAI", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(text = "Your Legal Companion")
            }
        }

        ChatList(messageList = viewModel.messageList, modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            TextField(
                singleLine = true,
                placeholder = { Text(text = "Enter your legal query", fontSize = 18.sp) },
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .clip(RoundedCornerShape(100))
                    .border(2.dp, color = Color.White.copy(0.8f), shape = RoundedCornerShape(100))
                    .fillMaxWidth(0.85f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(0.3f),
                    unfocusedContainerColor = Color.Gray.copy(0.5f),
                    unfocusedPlaceholderColor = Color.White.copy(0.5f)
                )
            )

            val scope = rememberCoroutineScope()

            IconButton(onClick = {

                focusManager.clearFocus()
                if(query.isNotEmpty()) {
                    viewModel.sendMessage(query)
                    query = " "

                    scope.launch {
                        delay(5000)
                        query = ""
                    }
                }
            }) {
                Icon(
                    modifier = Modifier.size(300.dp),
                    painter = painterResource(id = R.drawable.baseline_send_24),
                    contentDescription = null,
                    tint = Color.White.copy(0.8f)
                )
            }
        }

    }
}

@Composable
fun ChatList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Ask me anything", fontSize = 22.sp)
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(10.dp)
                ) {

                    MessageRow(messageModel = it)
                }
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {



            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(color = if(messageModel.role == "user") Color.White.copy(0.2f) else Color.White.copy(0.4f), RoundedCornerShape(20.dp))
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        5.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .padding(16.dp)
            ) {

                SelectionContainer {
                    Text(
                        fontSize = 14.sp,
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }


            }

        }


    }


}

