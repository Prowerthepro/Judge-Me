package com.socialscreencontrol.presentation.blocking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.socialscreencontrol.MainActivity

class BlockingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageNameBlocked = intent.getStringExtra(EXTRA_PACKAGE).orEmpty()
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("You reached today's limit.", style = MaterialTheme.typography.headlineSmall)
                    Text("$packageNameBlocked is blocked until your group approves extra time.")
                    Button(onClick = {
                        startActivity(Intent(this@BlockingActivity, MainActivity::class.java).apply {
                            putExtra("open_request", true)
                        })
                        finish()
                    }) {
                        Text("Request more time")
                    }
                    Button(onClick = { finishAffinity() }) { Text("Close") }
                }
            }
        }
    }

    companion object {
        const val EXTRA_PACKAGE = "extra_package"
    }
}
