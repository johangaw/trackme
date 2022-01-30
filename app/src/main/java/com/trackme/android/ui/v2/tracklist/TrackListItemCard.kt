package com.trackme.android.ui.v2.tracklist

import SwipeToRevealStage
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.trackme.android.TrackMeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import swipeToReveal
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun TrackListItemCard(
    item: TrackListItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    revealState: SwipeableState<SwipeToRevealStage> = rememberSwipeableState(
        SwipeToRevealStage.Hidden),
) {

    val buttonColor = MaterialTheme.colors.secondary
    var presence by remember { mutableStateOf(true) }
    val buttonSize = 48
    val buttonMargin = 8
    val visibleAnchorOffset =
        with(LocalDensity.current) { (buttonSize + 2 * buttonMargin).dp.toPx() }
    val animationDuration = 1_000
    val scope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = presence,
        exit = shrinkVertically(Alignment.Top,
                                animationSpec = tween(animationDuration, animationDuration))

    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                onClick = {
                    presence = false
                    scope.launch {
                        delay((animationDuration * 2).toLong())
                        onDelete()
                    }
                },
                shape = RoundedCornerShape(8.dp),
                color = buttonColor,
                modifier = Modifier
                    .padding(start = buttonMargin.dp)
                    .size(buttonSize.dp)
                    .animateEnterExit(exit = fadeOut(animationSpec = tween(animationDuration)))
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
                    .swipeToReveal(revealState, visibleAnchorOffset = visibleAnchorOffset)
                    .animateEnterExit(exit = slideOutHorizontally(targetOffsetX = { it },
                                                                  tween(animationDuration))),
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
}

@ExperimentalMaterialApi
@Preview
@Composable
fun TrackListItemCardPreview() {
    val tracks = remember {
        mutableStateListOf(
            TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
            TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
            TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
            TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
        )
    }

    TrackMeTheme {
        Column(Modifier
                   .padding(16.dp)
                   .background(Color.LightGray)) {
            tracks.forEachIndexed { index, track ->
                key(track, index) {
                    TrackListItemCard(track, {}, { tracks.remove(track) })
                    if (tracks.lastIndex != index) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}