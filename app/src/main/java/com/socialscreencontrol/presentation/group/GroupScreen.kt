package com.socialscreencontrol.presentation.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun GroupScreen(padding: PaddingValues, viewModel: GroupViewModel = hiltViewModel()) {
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val chat by viewModel.chat.collectAsStateWithLifecycle()
    var groupName by remember { mutableStateOf("") }
    var membersCsv by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Accountability Groups", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(groupName, { groupName = it }, label = { Text("Group name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        membersCsv,
                        { membersCsv = it },
                        label = { Text("Members (user IDs, comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.createGroup(
                                groupName,
                                membersCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Create group") }
                }
            }
        }

        items(groups, key = { it.id }) { group ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(group.name, style = MaterialTheme.typography.titleMedium)
                        Text("${group.memberIds.size} members", style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { viewModel.selectGroup(group.id) }) { Text("Open") }
                }
            }
        }

        item {
            Text("Group chat", style = MaterialTheme.typography.titleMedium)
        }

        items(chat, key = { it.id }) { msg ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(msg.senderId, style = MaterialTheme.typography.labelMedium)
                    Text(msg.content, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item {
            OutlinedTextField(message, { message = it }, label = { Text("Message") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { viewModel.sendMessage(message); message = "" }, modifier = Modifier.fillMaxWidth()) { Text("Send") }
        }
    }
}
