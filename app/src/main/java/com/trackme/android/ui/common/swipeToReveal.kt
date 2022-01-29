import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class SwipeToRevealStage {
    Hidden,
    Visible
}

@ExperimentalMaterialApi
@Composable
fun Modifier.swipeToReveal(
    swipeableState: SwipeableState<SwipeToRevealStage> = rememberSwipeableState(SwipeToRevealStage.Hidden),
    hiddenAnchorOffset: Float = 0f,
    visibleAnchorOffset: Float = 300f,
): Modifier {
    val anchors = mapOf(hiddenAnchorOffset to SwipeToRevealStage.Hidden,
                        visibleAnchorOffset to SwipeToRevealStage.Visible)
    return this.swipeable(
        state = swipeableState,
        anchors = anchors,
        thresholds = { _, _ -> FractionalThreshold(0.5f) },
        orientation = Orientation.Horizontal,
        resistance = ResistanceConfig(300f, 2f, 2f)
    )
}