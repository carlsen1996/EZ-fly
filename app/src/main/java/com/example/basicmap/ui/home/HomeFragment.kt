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
import kotlinx.android.synthetic.main.fragment_home.view.popupStub
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.coroutines.*
import java.util.*
import java.util.Calendar.DAY_OF_WEEK

private val TAG = "HomeFragment"

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var locationPermissionGranted = false
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var isSaved: Boolean = false

    // Transient reference to current marker, backed by model.position
    private var marker: Marker? = null
    private val zones = mutableListOf<Polygon>()

    private val model: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        if (savedInstanceState != null) {
            isSaved = true
        }

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

        // hack: the map handles most state itself
        if (isSaved)
            return

        map.setOnMapClickListener(this)
        // setMarker needs the map
        model.position.observe(viewLifecycleOwner, Observer {
            setMarker(it)
        })

        getLocationPermission()
        getDeviceLocation()

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
    }

    fun setMarker(p: LatLng): Marker {
        marker?.remove()
        val m = map.addMarker(MarkerOptions().position(p))
        marker = m

        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                val weather = Met().locationForecast(p)
                populatePopup(weather)
                displayAddressOfClickedArea(p)
            }
        }

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
    @SuppressLint("SetTextI18n")
    private fun populatePopup(weather: Met.Kall) {
        activity?.runOnUiThread {
            popup.visibility = View.VISIBLE
            popup.timeView.text = "Nå:"
            popup.windSpeedView.text = "Vindhastighet: ${weather.properties.timeseries[0].data.instant.details.wind_speed} m/s"
            popup.maxGustView.text = "Max vindkast: ${weather.properties.timeseries[0].data.instant.details.wind_speed_of_gust} m/s"
            popup.temperatureView.text = "Temperatur: ${weather.properties.timeseries[0].data.instant.details.air_temperature} °C"
            popup.precipitationView.text = "Regn: ${weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount} mm"
            popup.fogView.text = "Tåke: ${weather.properties.timeseries[0].data.instant.details.fog_area_fraction}%"
            popup.textView.text = "Klikk for neste dagers værvarsel"
            popup.setOnClickListener {
                val times = mutableListOf<Met.Numb>()
                val day = listOf("Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag")
                var dayNow = Calendar.getInstance().get(DAY_OF_WEEK) - 1
                for (i in 6..80) {
                    val time = weather.properties.timeseries[i].time
                    val hour1 = time[11]
                    val hour2 = time[12]
                    val hour = "${hour1}${hour2}"
                    if (hour == "12") {
                        times.add(weather.properties.timeseries[i])
                    }
                }
                dayNow++
                popup.timeView.text = "${day[dayNow]}:\n" +
                        "Vindhastighet: ${times[0].data.instant.details.wind_speed} m/s\n" +
                        "Max vindkast: ${times[0].data.instant.details.wind_speed_of_gust} m/s\n" +
                        "Temperatur: ${times[0].data.instant.details.air_temperature} °C\n" +
                        "Regn: ${times[0].data.next_6_hours.details.precipitation_amount} mm\n" +
                        "Tåke: ${times[0].data.instant.details.fog_area_fraction}%"
                dayNow++
                if (dayNow == 7) {
                    dayNow = 0
                }
                popup.windSpeedView.text = "${day[dayNow]}:\n" +
                        "Vindhastighet: ${times[1].data.instant.details.wind_speed} m/s\n" +
                        "Max vindkast: ${times[1].data.instant.details.wind_speed_of_gust} m/s\n" +
                        "Temperatur: ${times[1].data.instant.details.air_temperature} °C\n" +
                        "Regn: ${times[1].data.next_6_hours.details.precipitation_amount} mm\n" +
                        "Tåke: ${times[1].data.instant.details.fog_area_fraction}%"
                dayNow++
                if (dayNow == 7) {
                    dayNow = 0
                }
                if (times[2].data.next_6_hours.details.precipitation_amount == null) {
                    popup.maxGustView.text = "${day[dayNow]}\n" +
                            "Vindhastighet: ${times[2].data.instant.details.wind_speed} m/s\n" +
                            "Temperatur: ${times[2].data.instant.details.air_temperature} °C\n" +
                            "Regn: ${times[2].data.next_1_hours.details.precipitation_amount} mm\n"

                } else {
                    popup.maxGustView.text = "${day[dayNow]}:\n" +
                            "Vindhastighet: ${times[2].data.instant.details.wind_speed} m/s\n" +
                            "Temperatur: ${times[2].data.instant.details.air_temperature} °C\n" +
                            "Regn: ${times[2].data.next_6_hours.details.precipitation_amount} mm\n"
                }
                dayNow++
                if (dayNow == 7) {
                    dayNow = 0
                }
                if (times[3].data.next_6_hours.details.precipitation_amount == null) {
                    popup.temperatureView.text = "${day[dayNow]}:\n" +
                            "Vindhastighet: ${times[3].data.instant.details.wind_speed} m/s\n" +
                            "Temperatur: ${times[3].data.instant.details.air_temperature} °C\n" +
                            "Regn: ${times[3].data.next_1_hours.details.precipitation_amount} mm\n"
                } else {
                    popup.temperatureView.text = "${day[dayNow]}:\n" +
                            "Vindhastighet: ${times[3].data.instant.details.wind_speed} m/s\n" +
                            "Temperatur: ${times[3].data.instant.details.air_temperature} °C\n" +
                            "Regn: ${times[3].data.next_6_hours.details.precipitation_amount} mm\n"
                }

                dayNow++
                if (dayNow == 7) {
                    dayNow = 0
                }
                if (times[4].data.next_6_hours.details.precipitation_amount == null) {
                    popup.precipitationView.text = "${day[dayNow]}:\n" +
                            "Vindhastighet: ${times[4].data.instant.details.wind_speed} m/s\n" +
                            "Temperatur: ${times[4].data.instant.details.air_temperature} °C\n" +
                            "Regn: ${times[4].data.next_1_hours.details.precipitation_amount} mm\n"
                } else {
                    popup.precipitationView.text = "${day[dayNow]}:\n" +
                            "Vindhastighet: ${times[4].data.instant.details.wind_speed} m/s\n" +
                            "Temperatur: ${times[4].data.instant.details.air_temperature} °C\n" +
                            "Regn: ${times[4].data.next_6_hours.details.precipitation_amount} mm\n"
                }
                popup.fogView.text = ""
                popup.textView.text = "Klikk for å gå tilbake til kartet"
                popup.setOnClickListener{
                    popup.visibility = View.INVISIBLE
                }
            }
        }
    }
    /*
        Looks up location names using Geocoder.getFromLocation.
        When ready adds the name to the marker popup.
     */
    private fun displayAddressOfClickedArea(p: LatLng) {
        val geoc: Geocoder = Geocoder(activity)
        val locations = geoc.getFromLocation(p.latitude, p.longitude, 1)
        if (locations.size == 0)
            return

        // The following splits the string in order to remove unnecessary information. getAdressLine
        // returns very full info, for example
        // Frivoldveien 74, 4877, Grimstad, Norway.
        // Country name is a given, and therefore reduntant,
        // since our "marker" is limited to Norway (we use APIs for Norwegian weather only, and data
        // on restricted zones only for Norway
        val closestLocationAddress = locations[0].getAddressLine(0)
        val stringArray = closestLocationAddress?.split(",")?.toTypedArray()
        val addressToBeDisplayed = stringArray?.get(0) + "," + stringArray?.get(1)

        // Then textview is populated with address, postal code and city/place/location name
        activity?.runOnUiThread {
            popup.locationNameView.text = "Adresse: " + addressToBeDisplayed
        }
    }
}

