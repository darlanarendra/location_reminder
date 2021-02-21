package com.udacity.project4.locationreminders.savereminder.selectreminderlocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.locationreminders.geofence.GeofenceConstants

class SelectLocationViewModel() :
    ViewModel() {
    private val _selectedLocation = MutableLiveData<PointOfInterest>()
    private val _isRadiusSelectorOpen = MutableLiveData<Boolean>(false)
    val radius = MutableLiveData(GeofenceConstants.DEFAULT_RADIUS_IN_METERS)
    val selectedLocation:LiveData<PointOfInterest>
        get() = _selectedLocation

    val isRadiusSelectorOpen:LiveData<Boolean>
        get() = _isRadiusSelectorOpen

    fun setSelectedLocation(latLng:LatLng){
        _selectedLocation.postValue(PointOfInterest(latLng,null,null))
    }

    fun selectedLocation(pointOfInterest:PointOfInterest){
        _selectedLocation.postValue(pointOfInterest)
    }
    fun closedRadiusSelector() {
       _isRadiusSelectorOpen.postValue(false)
    }
}