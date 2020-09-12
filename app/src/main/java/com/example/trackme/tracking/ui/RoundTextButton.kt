package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import androidx.ui.tooling.preview.PreviewParameterProvider


@Composable
fun RoundTextButton(onClick: () -> Unit, text: String, modifier: Modifier = Modifier) {
    val buttonModifier = Modifier
        .clip(CircleShape)
        .clickable(onClick = onClick)
        .background(MaterialTheme.colors.primary)
        .padding(20.dp)
    Layout(
        modifier = modifier.then(buttonModifier),
        children = { Text(text = text, color = MaterialTheme.colors.onPrimary, style = MaterialTheme.typography.h1) }
    ) { measurables, constraints ->
        require(measurables.count() == 1)

        val placeable = measurables.first().measure(constraints)
        val size = maxOf(placeable.height, placeable.width, constraints.minHeight, constraints.minWidth)

        layout(size, size) {
            placeable.placeRelative((size - placeable.width) / 2, (size - placeable.height) / 2)
        }
    }
}


class SampleWordProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf("Start", "Done", "OK", "NOOOO")
}


@Composable
@Preview
fun RoundTextButtonPreview(@PreviewParameter(SampleWordProvider::class) text: String) {
    MaterialTheme {
        RoundTextButton({}, text)
    }
}
