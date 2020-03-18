package com.example.basicmap.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.basicmap.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var locationPermissionGranted = false
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val map = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)

        // NOTE: Have to use «!!» to declare non-null
        Places.initialize(context!!, getString(R.string.google_maps_key))
        placesClient = Places.createClient(context!!)
        locationClient = LocationServices.getFusedLocationProviderClient(context!!)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
        getLocationPermission()
        getDeviceLocation()
    }

    private fun getLocationPermission() {
        /* Adapted from https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java#L192

         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun getDeviceLocation() {
        locationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.apply {
                    Log.d("location", "got location ${latitude} ${longitude}")
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        // NOTE: use «x.xf» to get a java type float
                        LatLng(latitude, longitude), 15.0f
                    ))
                }
            } else {
                Log.d("location", "didn't get location")
            }
        }
    }

    override fun onMapClick(p0: LatLng?) {
        if (p0 == null)
            return
        setMarker(p0)
    }

    fun setMarker(p: LatLng): Marker {
        marker?.remove()
        val m = mMap.addMarker(MarkerOptions().position(p))
        marker = m
        popup.visibility = View.VISIBLE

        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        popup.textView.text = "${p.latitude}, ${p.longitude}"

        // Add place names to the popup
        // FIXME: Only reports names from the device location, make it actually react to the marker.
        val request = FindCurrentPlaceRequest.newInstance(placeFields)
        val placeResult = placesClient.findCurrentPlace(request)
        placeResult.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {

                val likely = it.result!!
                for (placeLikelihood in likely.placeLikelihoods) {
                    val place = placeLikelihood.place
                    popup.textView.text = "${popup.textView.text}\n${place.name}"
                }
            }
        }
        return m
    }
}
