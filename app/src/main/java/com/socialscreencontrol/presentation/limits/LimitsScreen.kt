package com.socialscreencontrol.presentation.limits

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.socialscreencontrol.core.model.AppLimit

@Composable
fun LimitsScreen(padding: PaddingValues, viewModel: LimitsViewModel = hiltViewModel()) {
    val limits by viewModel.limits.collectAsStateWithLifecycle()
    var packageName by remember { mutableStateOf("") }
    var appName by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("120") }

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
                    Text("App Limits", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(packageName, { packageName = it }, label = { Text("Package name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(appName, { appName = it }, label = { Text("Display name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        minutes,
                        { minutes = it },
                        label = { Text("Daily limit (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            viewModel.saveLimit(
                                AppLimit(
                                    packageName = packageName,
                                    appName = appName,
                                    dailyLimitMinutes = minutes.toIntOrNull() ?: 120
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Save limit") }
                }
            }
        }

        items(limits, key = { it.packageName }) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(it.appName, style = MaterialTheme.typography.titleMedium)
                    Text("${it.dailyLimitMinutes} minutes/day", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
