package com.trackme.android.data


sealed class Route(val route: String) {
    object TrackList: Route("tracks")
    object TrackDetails : Route("tracks/{trackId}") {
        val trackIdParam = "trackId"
        val deepLinkRoute = "https://trackme.com/tracks/{trackId}"
        fun createLink(trackId: Long) = route.replace("{$trackIdParam}", trackId.toString())
        fun createDeepLink(trackId: Long) = deepLinkRoute.replace("{$trackIdParam}", trackId.toString())
    }
}