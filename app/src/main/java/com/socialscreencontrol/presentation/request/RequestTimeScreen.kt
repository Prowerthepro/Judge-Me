package com.socialscreencontrol.presentation.request

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RequestTimeScreen(padding: PaddingValues, viewModel: RequestTimeViewModel = hiltViewModel()) {
    var groupId by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("20") }
    var reason by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Request Extra Time", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(groupId, { groupId = it }, label = { Text("Group ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(packageName, { packageName = it }, label = { Text("App package") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    minutes,
                    { minutes = it },
                    label = { Text("Extra minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(reason, { reason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth())
                Button(
                    onClick = { viewModel.submit(groupId, packageName, minutes.toIntOrNull() ?: 20, reason) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Send request") }
            }
        }
    }
}
