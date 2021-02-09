package com.udacity.project4.treasurehunt

import com.google.android.gms.maps.model.LatLng


data class LandmarkDataObject(val id:String, val hint:String, val name:String, val latLng:LatLng)


internal object GeofencingConstants{
    //val GEOFENCE_EXPIRATION_IN_MILLISECONDS :Long = TimeUnit.HOURS.toMillis(1)

    val LAND_MARK_DATA = arrayOf(
                    LandmarkDataObject("golden_gate_bridge", "Golden Gate Bridge", "Golden Gate Bridge", LatLng(37.819927,-122.478256)),
                    LandmarkDataObject("ferry_building", "Ferry Building", "Ferry Building", LatLng(37.819927,-122.394276)),
                    LandmarkDataObject("pier_39", "Pier 39", "Pier 39", LatLng(37.819927,-122.409821)),
                    LandmarkDataObject("union_square", "Union Square", "Union Square", LatLng(37.788151,-122.407570))
    )
    val NUM_LANDMARKS = LAND_MARK_DATA.size
    const val GEOFENCE_RADIUS = 100f
    //const val EXTRA_GEOFENCE_INDEX = "GEOFENCE_INDEX"
}