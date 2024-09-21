package com.arcmce.boogaloojetpack.network.model

data class RadioInfo(
    val currentTrack: Track
)

data class Track(
    val artworkUrlLarge: String
)