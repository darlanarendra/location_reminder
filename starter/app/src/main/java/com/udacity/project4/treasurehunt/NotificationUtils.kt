package com.udacity.project4.treasurehunt

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color.RED
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.GeofenceStatusCodes
import com.udacity.project4.R

val TAG: String = "NotificationUtils"
private const val NOTIFICATION_ID = 33
private const val CHANNEL_ID = "GeiFenceChannel"

fun errorMessage(context: Context, errorCode:Int):String{
    val resourses = context.resources
    return when(errorCode){
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE ->{
            resourses.getString(R.string.geo_fence_not_available)
        }
        else->{
            resourses.getString(R.string.geo_unknown_error)
        }
    }
}
fun createChannel(context: Context){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val notificationChannel = NotificationChannel(CHANNEL_ID,"GeofenceStatus",NotificationManager.IMPORTANCE_HIGH).apply {
            setShowBadge(false)
        }
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = RED
        notificationChannel.enableVibration(true)
        notificationChannel.description ="Geofence Status Notification"
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.sendGeofenceEnteredNotification(context:Context){
    Log.v(TAG,"sendGeofenceEnteredNotification")
    //val contentIntent = Intent(context,HuntMainActivity::class.java)
    //val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID,contentIntent,PendingIntent.FLAG_UPDATE_CURRENT)
    val bigPictureStyle = NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(context.resources,R.drawable.map_small)).bigLargeIcon(null)
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle("Treasure Hunt").setContentText("You Found the Clue").setSmallIcon(R.drawable.map_small)
        .setPriority(NotificationCompat.PRIORITY_HIGH).setStyle(bigPictureStyle)
    notify(NOTIFICATION_ID, builder.build())
}