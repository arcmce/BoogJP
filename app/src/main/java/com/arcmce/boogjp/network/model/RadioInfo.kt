package com.arcmce.boogjp.network.model

data class RadioInfo(
    val currentTrack: Track
)

data class Track(
    val artworkUrlLarge: String
)