package com.example.basicmap.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var locationPermissionGranted = false
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var marker: Marker? = null
    private val zones = mutableListOf<Polygon>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // NOTE: Have to use «!!» to declare non-null
        Places.initialize(context!!, getString(R.string.google_maps_key))
        placesClient = Places.createClient(context!!)
        locationClient = LocationServices.getFusedLocationProviderClient(context!!)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)
        getLocationPermission()
        getDeviceLocation()

        // Add a dummy zone
        addZone(listOf(
            LatLng(59.94195862876364, 10.76321694999933),
            LatLng(59.94377610821358, 10.762283876538277),
            LatLng(59.943641097920406, 10.759101770818233),
            LatLng(59.942160986342856, 10.754415281116962)
        ))
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
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(
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
        val m = map.addMarker(MarkerOptions().position(p))
        marker = m
        popup.visibility = View.VISIBLE

        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME, Place.Field.ADDRESS,
            Place.Field.LAT_LNG
        )
        val latlngText = "${p.latitude}, ${p.longitude}"
        popup.textView.text = latlngText
        Log.d("latlng", latlngText)

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

    fun addZone(positions: List<LatLng>): Polygon? {
        if (positions.size == 0)
            return null
        val polygonOptions = PolygonOptions().addAll(positions)
        polygonOptions.fillColor(Color.argb(40, 255, 0, 0))
        polygonOptions.strokeColor(Color.argb(180, 255, 0, 0))
        val polygon = map.addPolygon(polygonOptions)
        zones.add(polygon)
        return polygon
    }
}
