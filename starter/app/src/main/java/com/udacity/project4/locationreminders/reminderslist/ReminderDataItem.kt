package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import java.io.Serializable
import java.util.*

/**
 * data class acts as a data mapper between the DB and the UI
 */
data class ReminderDataItem(
    var title: String?,
    var description: String?,
    var location: String?,
    var latitude: Double?,
    var longitude: Double?,
    val radius:Float?,
    val id: String = UUID.randomUUID().toString()
) : Serializable {

    val printableLocation: String
        get() {
            if (location != null) {
                return location as String
            }

            return "Lat: $latitude Lon: $longitude"
        }
}

fun ReminderDataItem.toDTO() = ReminderDTO(
    title,description,location,latitude,longitude,radius
)