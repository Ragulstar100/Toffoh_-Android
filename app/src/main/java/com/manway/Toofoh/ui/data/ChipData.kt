package Ui.data

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class ChipData(
    val label: String,
    var isChecked: Boolean = false
)

@Composable
fun Chip(chipData: ChipData, onCheckedChange: (ChipData) -> Unit) {
    Surface(shape = RoundedCornerShape(8.dp), color = if (chipData.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface), modifier = Modifier.clickable { onCheckedChange(chipData) }.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text = chipData.label, color = if (chipData.isChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun Chip(chipData: ChipData, onCheckedChange: (ChipData) -> Unit, chipContent:@Composable ()->Unit) {
    Surface(shape = RoundedCornerShape(8.dp), color = if (chipData.isChecked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface), modifier = Modifier.clickable { onCheckedChange(chipData) }.padding(horizontal = 8.dp, vertical = 4.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(text = chipData.label, color = if (chipData.isChecked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun ChipGroup(chips:MutableState<List<ChipData>>, onCheckedChange: (ChipData) -> Unit) {
    Row {
        chips.value.forEach { chip ->
            Chip(chipData = chip, onCheckedChange ={chips.value=chips.value.map { item->
                if(item.label==chip.label) item.copy(isChecked = true) else item.copy(isChecked = false)
            } ;onCheckedChange(chip)})
        }
    }
}

//Not Completed
@Composable
fun ChipGroup(modifier: Modifier, chips:MutableState<List<ChipData>>, onCheckedChange:  (ChipData) -> Unit, content:@Composable (ChipData)->Unit) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        chips.value.forEach { chip ->
            Box(Modifier.clickable {
                chips.value=chips.value.map { item->
                    if(item.label==chip.label) item.copy(isChecked = true) else item.copy(isChecked = false)
                } ;onCheckedChange(chip)
            }) {
                content(chip)
            }

        }
    }
}