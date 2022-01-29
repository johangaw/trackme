package com.trackme.android.ui.v2.tracklist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackme.android.TrackMeTheme
import kotlin.math.max

private val CutoutPlusShape = GenericShape { size, _ ->
    val cutOut = Path().apply {
        val lineWidth = 10f
        val padding = max(size.width, size.height) * 0.3f
        addRect(Rect(Offset(padding, size.height / 2f - lineWidth / 2f),
                     Offset(size.width - padding, size.height / 2f + lineWidth / 2f)))

        addRect(Rect(Offset(size.width / 2f - lineWidth / 2f, padding),
                     Offset(size.width / 2f + lineWidth / 2f, size.height - padding)))
        close()
    }

    addOval(Rect(Offset.Zero, Offset(size.width, size.height)))

    op(this, cutOut, PathOperation.Difference)
}

@Composable
fun ListFAB(
    onClick: () -> Unit,
) {
    Surface(
        shape = CutoutPlusShape,
        color = MaterialTheme.colors.secondary.copy(alpha = 0.8f),
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClickLabel = "Add track", onClick = onClick)
    ) {}
}


@Composable
@Preview(showBackground = true)
fun ListFABPreview() {
    TrackMeTheme {
        ListFAB() {}
    }
}

@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
fun ListFABInContextPreview() {
    TrackMeTheme {
        Scaffold(
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                ListFAB() {}
            }
        ) {}
    }
}
