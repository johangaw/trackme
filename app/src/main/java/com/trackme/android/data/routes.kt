package com.trackme.android.data


sealed class Route(val route: String) {
    object TrackList: Route("tracks")
    object TrackDetails : Route("tracks/{trackId}") {
        const val trackIdParam = "trackId"
        const val deepLinkRoute = "https://trackme.com/tracks/{trackId}"
        fun createLink(trackId: Long) = route.replace("{$trackIdParam}", trackId.toString())
        fun createDeepLink(trackId: Long) = deepLinkRoute.replace("{$trackIdParam}", trackId.toString())
    }
    object TrackMap : Route("tracks/{trackId}/map") {
        const val trackIdParam = "trackId"
        const val deepLinkRoute = "https://trackme.com/tracks/{trackId}/map"
        fun createLink(trackId: Long) = route.replace("{$trackIdParam}", trackId.toString())
        fun createDeepLink(trackId: Long) = deepLinkRoute.replace("{$trackIdParam}", trackId.toString())
    }
}