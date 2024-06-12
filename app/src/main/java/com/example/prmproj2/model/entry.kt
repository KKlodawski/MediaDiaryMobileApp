package com.example.prmproj2.model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import java.lang.Exception
import java.util.Locale

data class entry(
    val entryId: Int,
    val note: String,
    val location: Location?,
    val imageId: Int?,
    val voiceId: Int?
) {
    fun getNote(context: Context): String {
        return location?.let { loc ->
            val latitude = loc.latitude
            val longitude = loc.longitude
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude,longitude,1)
                if(!addresses.isNullOrEmpty()) {
                    "$note \n ${addresses[0].locality}"
                } else {
                    note
                }
            }catch (e: Exception) {
                note
            }
        } ?: note

    }
}