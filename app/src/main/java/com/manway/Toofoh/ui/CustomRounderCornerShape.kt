package com.manway.toffoh.admin.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp



class CustomRounderCornerShape( val customSize: Size = Size.Zero ,val cornerRadius: Dp = 16.dp
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val cornerRadiusPx = with(density) { cornerRadius.toPx() }
            val width = if (customSize.width != 0f) customSize.width else size.width
            val height = if (customSize.height != 0f) customSize.height else size.height

            // Top left corner
            arcTo(
                rect = Rect(
                    Offset(0f, 0f),
                    Size(cornerRadiusPx * 2, cornerRadiusPx * 2)
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Top right corner
            lineTo(width - cornerRadiusPx, 0f)
            arcTo(
                rect = Rect(
                    Offset(width - cornerRadiusPx * 2, 0f),
                    Size(cornerRadiusPx * 2, cornerRadiusPx * 2)
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f,
                forceMoveTo = false
            )

            // Bottom right corner
            lineTo(width, height)

            // Bottom left corner
            lineTo(0f, height)
            close()
        }
        return Outline.Generic(path)
    }
}
