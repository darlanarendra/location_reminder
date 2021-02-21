package com.udacity.project4.locationreminders.savereminder.selectreminderlocation



import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1

    //Use Koin to get the view model of the SaveReminder
    private val TAG = SelectLocationFragment::class.java.simpleName
    override val _viewModel: SaveReminderViewModel by inject()
    private val selectLocationViewModel: SelectLocationViewModel by viewModel()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map:GoogleMap
    private lateinit var selectedLocationMarker : Marker
    private lateinit var selectedLocationCircle: Circle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = selectLocationViewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        setupGoogleMap()
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected


//        TODO: call this function after the user confirms on the selected location
        selectLocationViewModel.selectedLocation.observe(viewLifecycleOwner, Observer {
            Log.v(TAG, "SelectLocationFragment"+it.latLng.longitude+":"+it.latLng.latitude)
            selectedLocationMarker.position = it.latLng
            selectedLocationCircle.center = it.latLng
            setCamera(it.latLng)
        })
        binding.onSaveButtonClicked = View.OnClickListener {
            onLocationSelected()
        }

        return binding.root
    }

    private fun setupGoogleMap() {
        val mapFragment = childFragmentManager.findFragmentByTag("map_fragment") as SupportMapFragment?:return
        mapFragment.getMapAsync(this)

    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        selectLocationViewModel.closedRadiusSelector()
        _viewModel.setselectedLocation(selectLocationViewModel.selectedLocation.value)
        _viewModel.setSelectedRadius(selectLocationViewModel.radius.value!!)
        _viewModel.navigationCommand.postValue(NavigationCommand.Back)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            this.map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            this.map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            this.map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            this.map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(map: GoogleMap) {

        this.map = map
        this.map.setMapStyle(
           MapStyleOptions.loadRawResourceStyle(requireContext(),R.raw.map_style)
        )
        setCamera(getCurrentLocation())
        val markerOptions = MarkerOptions().position(map.cameraPosition.target).title(getString(R.string.dropped_pin)).draggable(true)
        selectedLocationMarker = map.addMarker(markerOptions)
        selectedLocationMarker.position = getCurrentLocation()
        this.map.setOnMapClickListener {
            setCamera(it)
            selectLocationViewModel.setSelectedLocation(it)
        }
        this.map.uiSettings.isZoomControlsEnabled = true
        enableLocation()
        val circleOptions = CircleOptions().center(map.cameraPosition.target).fillColor(ResourcesCompat.getColor(resources,R.color.colorAccent,null))
            .strokeColor(ResourcesCompat.getColor(resources,R.color.black,null))
            .strokeWidth(4f)
            .radius(300f.toDouble())
        selectedLocationCircle = map.addCircle(circleOptions)
        _viewModel.selectedPOI.value.let {
            if(it == null){
                startCurrentLocation()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun startCurrentLocation() {
        if(!LocationUtils.hasLocationPermissions()){
            LocationUtils.requestPermissions{
                locationPermissionHandler(it,this::startCurrentLocation)
            }
            return
        }

        fun resetToCurrentLocation() =
            LocationUtils.requestSingleUpdate {
                selectLocationViewModel.setSelectedLocation(it.toLatLng())
            }
        map.isMyLocationEnabled = true
        map.setOnMyLocationButtonClickListener {
            selectLocationViewModel.closedRadiusSelector()
            resetToCurrentLocation()
            true
        }
        resetToCurrentLocation()
    }

    private fun locationPermissionHandler(event: PermissionsResultEvent, handler: ()->Unit) {
            if(event.areAllGranted){
                handler()
                return
            }
        if(event.shouldShowRequestRational){
            _viewModel.showSnackBar.postValue("You Need to Grant Location Permission in order to select the location ")
        }
    }

    private fun getCurrentLocation() = LatLng(37.8199328, -122.4804438)

    fun setCamera(location:LatLng){
        val cameraPosition = CameraPosition.fromLatLngZoom(location, 15.5f)
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        map.animateCamera(cameraUpdate)
    }

    fun enableLocation(){
        if(ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(activity!!,arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }else{
            this.map.setMyLocationEnabled(true)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_LOCATION_PERMISSION){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                enableLocation()
                Log.v(TAG, " You have been Granted Permission ")
            }
        }
    }
}
