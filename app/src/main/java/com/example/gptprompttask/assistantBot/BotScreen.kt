package com.example.gptprompttask.assistantBot

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotScreen(viewModel: BotViewModel) {
    val allSentMessage = viewModel.myMessages
    val allResponses = viewModel.allResponses

    var currentMessage by remember {
        mutableStateOf("")
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.45f)
                .padding(16.dp),
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                allResponses.forEach {
                    Surface(
                        tonalElevation = 10.dp,
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Text(
                            text = it,
                            modifier =
                            Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5f)
                .padding(16.dp),
            tonalElevation = 4.dp,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                allSentMessage.forEach {
                    Surface(
                        tonalElevation = 10.dp,
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.padding(4.dp),
                    ) {
                        Text(
                            text = it,
                            modifier =
                            Modifier.padding(8.dp),
                        )
                    }
                }
            }
        }

        TextField(
            value = currentMessage,
            onValueChange = {
                currentMessage = it
            },
            singleLine = false,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
        )

        Button(onClick = {
            viewModel.sendMessage(currentMessage)
            currentMessage = ""
        }) {
            Text(text = "Send Message")
        }
    }
}
