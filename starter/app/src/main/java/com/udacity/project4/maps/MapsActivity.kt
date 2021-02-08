package com.udacity.project4.maps

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.udacity.project4.R
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = MapsActivity::class.java.simpleName
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLoction()
        setMapLongClick(map)
    }

    private fun enableMyLoction() {
        Log.v(TAG,"enableMyLoction"+isPermissionGranted())

            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PERMISSION_GRANTED
            ) {

                    ActivityCompat.requestPermissions(this, arrayOf<String>(ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
                return
            }
            map.setMyLocationEnabled(true)


    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnPoiClickListener { pointOfInterest ->
           map.addMarker(MarkerOptions().position(pointOfInterest.latLng).title(pointOfInterest.name)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
           map.addGroundOverlay(GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.common_full_open_on_phone)).position(pointOfInterest.latLng,100f))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.map_options,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.normal_map->{
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.satellite_map ->{
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map ->{
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        R.id.hybrid_map->{
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        else ->  super.onOptionsItemSelected(item)
    }


    private fun isPermissionGranted():Boolean{
       return ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.size>0 && grantResults[0] == PERMISSION_GRANTED){
                enableMyLoction()
            }
        }
    }
}