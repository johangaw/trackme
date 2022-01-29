package com.trackme.android.ui.v2.tracklist

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackme.android.R
import com.trackme.android.TrackMeTheme

@Composable
fun TrackListScreen() {
    Scaffold(
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            ListFAB(onClick = { /* TODO */ })
        }
    ) {
        val tracks = mapOf(
            "Week 1" to listOf(TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
                              TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
                              TrackListItem("00:34:45", "5.43 km", "2,56 m/s")),
            "Week 2" to listOf(TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
                              TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
                              TrackListItem("00:34:45", "5.43 km", "2,56 m/s")),
            "Week 3" to listOf(TrackListItem("00:34:45", "5.43 km", "2,56 m/s"),
                              TrackListItem("00:34:45", "5.43 km", "2,56 m/s"))
        )

        Column(
            Modifier.verticalScroll(rememberScrollState()),
        ) {
            HeroHeader()
            Spacer(modifier = Modifier.height(16.dp))
            Column(Modifier.padding(horizontal = 8.dp)) {
                MonthCarousel()
                Spacer(modifier = Modifier.height(24.dp))
                TrackList(tracks)
            }
        }
    }
}

@Composable
fun HeroHeader() {
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(painterResource(R.drawable.list_hero),
              modifier = Modifier.fillMaxWidth(),
              contentDescription = null,
              contentScale = ContentScale.FillWidth)
        Text("2021", style = MaterialTheme.typography.h2)
    }
}

@Composable
fun MonthCarousel() {
    // TODO: Add scroll
    // TODO: Add state
    val sideTextStyle = MaterialTheme.typography.h3.copy(
        color = MaterialTheme.typography.h3.color.copy(alpha = 0.5f)
    )
    Row(Modifier.fillMaxWidth(),
        Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Text("December", modifier = Modifier.weight(1f), style = sideTextStyle)
        Text("Januari",
             modifier = Modifier.weight(1f),
             style = MaterialTheme.typography.h2,
             textAlign = TextAlign.Center)
        Text("Februari",
             modifier = Modifier.weight(1f),
             style = sideTextStyle,
             textAlign = TextAlign.End)
    }

}


data class TrackListItem(
    val duration: String,
    val length: String,
    val averageSpeed: String,
)


@Composable
fun TrackList(
    tracks: Map<String, List<TrackListItem>>,
) {

    tracks.forEach { (week, tracks) ->
        Text(week, style = MaterialTheme.typography.h3)
        Spacer(Modifier.height(16.dp))

        tracks.forEachIndexed { index, item ->
            TrackListItemCard(item)

            val spacing = if (index == tracks.lastIndex) 24 else 16
            Spacer(Modifier.height(spacing.dp))
        }
    }
}

@Composable
fun TrackListItemCard(item: TrackListItem) {
    Card(
        Modifier.fillMaxWidth(),
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


@Composable
@Preview(device = Devices.PIXEL_4, showSystemUi = true)
fun TrackListPreview() {
    TrackMeTheme {
        TrackListScreen()
    }
}