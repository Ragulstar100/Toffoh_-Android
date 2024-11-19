package com.manway.Toofoh.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingFilter(
    modifier: Modifier = Modifier,
    optionLabels: List<String>,
    options: List<Double>,
    selectedRating: Double,
    onRatingSelected: (Double) -> Unit,
) {
//    val options = listOf(-1.0, 3.0, 4.0, 4.5)
//    val optionLabels = listOf("none", "3.0+", "4.0+", "4.5+")

    Row(
        modifier = modifier
            .width(180.dp)
            .height(35.dp)
            .border(1.dp, Color.LightGray, MaterialTheme.shapes.medium)
            .background(Color.LightGray, MaterialTheme.shapes.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, rating ->
            RatingOption(
                label = optionLabels[index],
                isSelected = selectedRating == rating,
                onClick = { onRatingSelected(rating) }
            )
            if (index < options.size - 1) {
                VerticalDivider(Modifier.fillMaxHeight(0.90f))
            }
        }
    }
}

@Composable
fun RatingOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(if (isSelected) Color.LightGray else Color.White)
            .padding(3.dp)
    )
}