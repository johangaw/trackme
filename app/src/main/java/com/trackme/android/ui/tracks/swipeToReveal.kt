import androidx.compose.foundation.gestures.Orientation
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@ExperimentalMaterialApi
@Composable
fun Modifier.swipeToReveal(
    swipeableState: SwipeableState<String> = rememberSwipeableState("hidden"),
): Modifier {
    val anchors = mapOf(0f to "hidden", 300f to "visible")
    return this.swipeable(
        state = swipeableState,
        anchors = anchors,
        thresholds = { _, _ -> FractionalThreshold(0.5f) },
        orientation = Orientation.Horizontal,
        resistance = ResistanceConfig(300f, 2f, 2f)
    )
}