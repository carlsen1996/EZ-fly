package com.example.basicmap.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.basicmap.R
import com.example.basicmap.lib.Met
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
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.coroutines.*
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var locationPermissionGranted = false
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient

    // Transient reference to current marker, backed by model.position
    private var marker: Marker? = null
    private val zones = mutableListOf<Polygon>()

    private val model: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Make the
        root.popupStub.inflate()

        // NOTE: Have to use «!!» to declare non-null
        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener(this)
        getLocationPermission()
        getDeviceLocation()

        // setMarker needs the map
        model.position.observe(viewLifecycleOwner, Observer {
            setMarker(it)
        })

        // Add a dummy zone
        addZone(
            listOf(
                LatLng(59.94195862876364, 10.76321694999933),
                LatLng(59.94377610821358, 10.762283876538277),
                LatLng(59.943641097920406, 10.759101770818233),
                LatLng(59.942160986342856, 10.754415281116962)
            )
        )
    }

    private fun getLocationPermission() {
        /* Adapted from https://github.com/googlemaps/android-samples/blob/master/tutorials/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java#L192

         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun getDeviceLocation() {
        locationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result?.apply {
                    Log.d("location", "got location ${latitude} ${longitude}")
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            // NOTE: use «x.xf» to get a java type float
                            LatLng(latitude, longitude), 15.0f
                        )
                    )
                }
            } else {
                Log.d("location", "didn't get location")
            }
        }
    }

    override fun onMapClick(p0: LatLng?) {
        if (p0 == null)
            return
        model.position.value = p0
        var weather: Met.Kall
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                weather = Met().locationForecast(p0)
                populatePopup(weather)
                displayAddressOfClickedArea(p0)
            }

        }


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
        //popup.textView.text = latlngText
        Log.d("latlng", latlngText)

        // Add place names to the popup
        // FIXME: Only reports names from the device location, make it actually react to the marker.
        val request = FindCurrentPlaceRequest.newInstance(placeFields)
        val placeResult = placesClient.findCurrentPlace(request)
        placeResult.addOnCompleteListener {
            // FIXME: destroy the listener when the fragment is destroyed
            // This can trigger from a dead fragment after a rotation, so guard
            // against dead views
            if (popup == null)
                return@addOnCompleteListener
            if (it.isSuccessful && it.result != null) {

                val likely = it.result!!
                for (placeLikelihood in likely.placeLikelihoods) {
                    val place = placeLikelihood.place
                    //popup.textView.text = "${popup.textView.text}\n${place.name}"
                    break // Limit to one result
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

    private fun populatePopup(weather: Met.Kall) {
        activity?.runOnUiThread {
            popup.windSpeedView.text =
                "Vindhastighet: ${weather.properties.timeseries[0].data.instant.details.wind_speed} m/s"
            popup.maxGustView.text =
                "Max vindkast: ${weather.properties.timeseries[0].data.instant.details.wind_speed_of_gust} m/s"
            popup.temperatureView.text =
                "Temperatur: ${weather.properties.timeseries[0].data.instant.details.air_temperature} °C"
            popup.precipitationView.text =
                "Regn: ${weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount} mm"
            popup.fogView.text =
                "Tåke: ${weather.properties.timeseries[0].data.instant.details.fog_area_fraction}%"

        }

    }

    private fun displayAddressOfClickedArea(p0: LatLng?) {
        activity?.runOnUiThread {

            //This method first finds latitude and longitude of p0, which is an instance of the LatLng-class.
            //Then we instantiate a geocoder-object, and use it to receive all the names of addresses associated with mentioned lat and long.
            //the name of the first address is then displayed, which we determine to be the closest one to the latLang-coordinates

            var lat: Double? = p0?.latitude
            var long: Double? = p0?.longitude

            var geoc: Geocoder = Geocoder(this.activity, Locale.ENGLISH) //is the problem here, not accessing right activity?
            var locationList = mutableListOf<Address>()

            var closestLocationAddress: String? = ""

            if (lat != null && long != null) {
                //locationList is a mutable list that adds all elements in an array of Addresses. Because geoc(geocoder).getFromLocation returns just that.
                locationList.addAll(geoc.getFromLocation(lat, long, 1))
            }

            if (locationList != null) {
                closestLocationAddress = locationList[0]?.getAddressLine(0) //why does this not work? Perhaps we need new google maps API-key-thing?
            }

            // the following splits the string in order to remove unnecessary information. getAdressLine returns very full info, for example:
            // Frivoldveien 74, 4877, Grimstad, Norway. Country name is a given, and therefore reduntant,
            // since our "market" is limited to Norway (we use APIs for Norwegian weather only, and data on restricted zones only for Norway

            val stringArray = closestLocationAddress?.split(",")?.toTypedArray()
            val addressToBeDisplayed: String? = stringArray?.get(0) + "," + stringArray?.get(1)

            //Then textview is populated with address, postal code and city/place/location name
            popup.locationNameView.text = "Adresse: " + addressToBeDisplayed //closestLocationName
        }
    }
}


