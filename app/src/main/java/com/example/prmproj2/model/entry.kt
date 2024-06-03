package com.example.prmproj2.model

import android.location.Location

data class entry(
    val entryId: Int,
    val note: String,
    val location: Location?,
    val imageId: Int,
    val voiceId: Int
)