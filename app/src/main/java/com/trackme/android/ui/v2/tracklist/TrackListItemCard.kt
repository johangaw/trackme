package com.trackme.android.ui.v2.tracklist

import SwipeToRevealStage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.trackme.android.TrackMeTheme
import swipeToReveal
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TrackListItemCard(
    item: TrackListItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    revealState: SwipeableState<SwipeToRevealStage> = rememberSwipeableState(
        SwipeToRevealStage.Hidden),
) {
    val buttonColor = MaterialTheme.colors.secondary
    Box(
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            onClick = onDelete,
            shape = RoundedCornerShape(8.dp),
            color = buttonColor,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(48.dp)
        ) {
            Box(Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove track"
                )
            }
        }
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(revealState.offset.value.roundToInt(), 0) }
                .swipeToReveal(revealState, visibleAnchorOffset = 200f),
            elevation = 6.dp,
        ) {
            Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.duration, style = MaterialTheme.typography.subtitle1)
                Text(item.length, style = MaterialTheme.typography.subtitle1)
                Text(item.averageSpeed, style = MaterialTheme.typography.subtitle1)
            }
        }
    }

}

@ExperimentalMaterialApi
@Preview
@Composable
fun TrackListItemCardPreview() {
    TrackMeTheme {
        Column(Modifier.padding(16.dp)) {
            TrackListItemCard(TrackListItem("00:34:45", "5.43 km", "2,56 m/s"), {}, {})
            Spacer(modifier = Modifier.height(8.dp))
            TrackListItemCard(
                TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
                {},
                {},
                rememberSwipeableState(SwipeToRevealStage.Visible)
            )
        }
    }

}