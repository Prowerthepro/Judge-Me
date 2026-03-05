package com.socialscreencontrol.presentation.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialscreencontrol.core.util.minutesAsReadable

@Composable
fun LeaderboardScreen(padding: PaddingValues, viewModel: LeaderboardViewModel = hiltViewModel()) {
    var groupId by remember { mutableStateOf("") }
    val entries by viewModel.entries.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Leaderboard", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(groupId, { groupId = it; viewModel.bindGroup(it) }, label = { Text("Group ID") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }
        items(entries, key = { it.userId }) { entry ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("#${entry.rank} ${entry.userName}", style = MaterialTheme.typography.titleMedium)
                    Text(entry.totalMinutesToday.minutesAsReadable(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
