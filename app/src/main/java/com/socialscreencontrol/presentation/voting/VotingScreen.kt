package com.socialscreencontrol.presentation.voting

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun VotingScreen(padding: PaddingValues, viewModel: VotingViewModel = hiltViewModel()) {
    var groupId by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val requests by viewModel.requests.collectAsStateWithLifecycle()

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
                    Text("Pending Votes", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(groupId, { groupId = it; viewModel.bindGroup(it) }, label = { Text("Group ID") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(comment, { comment = it }, label = { Text("Comment") }, modifier = Modifier.fillMaxWidth())
                }
            }
        }

        items(requests, key = { it.id }) { request ->
            Card(modifier = Modifier.fillMaxWidth().animateContentSize()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${request.requesterId} requests ${request.extraMinutes} min for ${request.packageName}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(request.reason, style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.vote(request, approve = true, comment = comment) }, modifier = Modifier.weight(1f)) {
                            Text("Approve")
                        }
                        Button(onClick = { viewModel.vote(request, approve = false, comment = comment) }, modifier = Modifier.weight(1f)) {
                            Text("Reject")
                        }
                    }
                }
            }
        }
    }
}
