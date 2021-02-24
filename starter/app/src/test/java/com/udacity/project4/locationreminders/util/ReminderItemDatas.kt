package com.udacity.project4.locationreminders.util

import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

val dataItemWithOutRadius = ReminderDataItem(
    "title",
    "description",
    "location",
    2.213123123,
    1.23333,
    null
)


val dataItemWithoutLocationCoordinates = ReminderDataItem(
    "title",
    "description",
    "locartion",
    null,
    null,
    300f
)
val dataItemWithOutTitle = ReminderDataItem(
    null,
    "description",
    "location",
    1.23,
    2.0,
    300f
)


val dataItemWithAllNulls = ReminderDataItem(
    null,
    null,
    null,null,null,null
)
val dataItemValid = ReminderDataItem(
    "title",
    "description",
    "location",
    1.23,
    2.43,
    300f
)