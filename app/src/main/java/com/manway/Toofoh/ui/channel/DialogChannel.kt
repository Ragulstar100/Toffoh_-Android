package com.manway.Toofoh.ui.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.channels.Channel

var dialogChannel = Channel<RDialog>()

data class RDialog(var title: String, var value: String, var ignore: Boolean = false) {
    companion object {
        val initial = RDialog("", "")
    }

    @Composable
    fun openDialog(ingnoreAction: () -> Unit) {
        if (!ignore) Dialog({}, DialogProperties()) {
            Column(
                Modifier
                    .width(350.dp)
                    .heightIn(100.dp)
                    .background(Color.White, MaterialTheme.shapes.small)
            ) {
                Text(
                    title,
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    value,
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.0.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        ignore = true
                        ingnoreAction()
                    }) {
                        Text("Ingnore")
                    }
                }
            }
        }
    }

}