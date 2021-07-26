package jez.jetpackpop.ui

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

@Stable
fun RoundedRectangleShape(cornerRadius: CornerRadius): Shape =
    object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
            Outline.Rounded(RoundRect(size.toRect(), cornerRadius))

        override fun toString(): String = "RoundedRectangleShape"
    }
