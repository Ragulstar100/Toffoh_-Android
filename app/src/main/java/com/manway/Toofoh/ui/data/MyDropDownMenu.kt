package Ui.data

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun MyDropdownMenu(intialString: String,items: List<String>, modifier: Modifier, onSelect:(String)->Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(intialString) }

    Box {
        TextButton(onClick = { expanded = !expanded }) { Text(text = selectedItem) }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier =modifier) {
            items.forEach { item ->
                DropdownMenuItem({ Text(text = item) },onClick = {
                    selectedItem = item
                    onSelect(item)
                    expanded = false
                })
            }
        }
    }
}