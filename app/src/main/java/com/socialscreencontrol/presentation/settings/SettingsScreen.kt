package com.socialscreencontrol.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.socialscreencontrol.core.util.AppNotifier

@Composable
fun SettingsScreen(padding: PaddingValues) {
    var notifications by remember { mutableStateOf(true) }
    var strictMode by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Push notifications", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = notifications, onCheckedChange = { notifications = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Strict blocking mode", style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = strictMode, onCheckedChange = { strictMode = it })
                }
                Button(
                    onClick = {
                        AppNotifier.notify(
                            context = context,
                            title = "Social Screen Control",
                            body = "This is a real test notification from your app."
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send test notification")
                }
                Button(
                    onClick = {
                        val mailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse(
                                "mailto:asserfarra11@gmail.com" +
                                    "?subject=" + Uri.encode("Social Screen Control - Report") +
                                    "&body=" + Uri.encode("Describe your issue here:")
                            )
                        }
                        val packageManager = context.packageManager
                        if (mailIntent.resolveActivity(packageManager) != null) {
                            context.startActivity(mailIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Report a problem")
                }
            }
        }
    }
}
