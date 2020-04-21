package com.example.basicmap.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesViewModel
import com.example.basicmap.lib.getJsonDataFromAsset
import com.example.basicmap.lib.initNoFlyLufthavnSirkel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.fragment_home.view.popupStub
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.coroutines.*
import java.util.*
import java.util.Calendar.DAY_OF_WEEK
import kotlin.coroutines.CoroutineContext

private val TAG = "HomeFragment"

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener, CoroutineScope {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var locationPermissionGranted = false
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient

    // Transient reference to current marker, backed by model.position
    private var marker: Marker? = null
    private val zones = mutableListOf<Polygon>()

    private val model: HomeViewModel by viewModels()
    private val placesViewModel: PlacesViewModel by viewModels()

    // Dummy job to make cancellation of running jobs easy
    private lateinit var job: Job

    // Add all jobs to the same context
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    private var sirkelMutableListOver = mutableListOf<CircleOptions>()
    private var ferdigsirkelMutableListOver = mutableListOf<Circle>()
    private var lufthavnButtonTeller = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onDestroyView() {
        // Not sure why this works, but savedInstanceState doesn't
        // Save the camera position, so we don't unnecessarily reset on onCreateView
        model.cameraPosition = map.cameraPosition
        job.cancel() // Cancel all running jobs
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

        job = Job()

        mapFragment.getMapAsync(this)

        // viewStubs needs to be inflated
        root.popupStub.inflate()
        model.address.observe(viewLifecycleOwner, Observer {
            if (it == "")
                popup.locationNameView.text = "Ingen addresseinformasjon tilgjengelig"
            else
                popup.locationNameView.text = "Adresse: " + it
        })
        model.weather.observe(viewLifecycleOwner, Observer {
            populatePopup(it)
        })

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        root.lagreLokasjonsKnapp.setOnClickListener {
            // This is only accessible from the popup, meaning the position has been set
            val place = model.place.value!!
            place.address = model.address.value ?: ""
            val places = placesViewModel.getPlaces().value!!
            val toast = Toast.makeText(
                activity,
                "",
                Toast.LENGTH_SHORT
            )
            if (place.favorite) {
                toast.setText("${model.address.value}, er fjernet fra favoritter")
                place.favorite = false
                places.remove(place)
            } else {
                toast.setText("${model.address.value}, er lagt til i favoritter")
                place.favorite = true
                places.add(place)
            }
            placesViewModel.getPlaces().value = places
            toast.show()
        }

        return root
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener(this)
        if (model.cameraPosition == null) {
            getLocationPermission()
            getDeviceLocation()
        }

        leggTilLufthavner()
        flyplassButton.setOnClickListener {
            if (lufthavnButtonTeller == false) {
                fjernLufthavner()
                Log.d("KartLufthavnButton", "fjerner lufthavner")
            }
            else if (lufthavnButtonTeller == true) {
                leggTilLufthavner()
                Log.d("KartLufthavnButton", "legger til lufthavner")
            }
        }

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

    override fun onMapClick(p: LatLng?) {
        if (p == null)
            return
        model.place.value = Place(p)
        marker?.remove()
        marker = map.addMarker(MarkerOptions().position(p))
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
                for (i in 6..75) {
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

    private fun leggTilLufthavner() {
        if (sirkelMutableListOver.size == 0) {
            val jsonFilStringen = getJsonDataFromAsset(requireContext(), "lufthavnRawJson.json")
            sirkelMutableListOver = initNoFlyLufthavnSirkel(jsonFilStringen)
        }

        // Avoid redrawing circles
        if (!lufthavnButtonTeller)
            return

        for (optionini in sirkelMutableListOver) {
            val sorkel: Circle = map.addCircle((optionini))
            ferdigsirkelMutableListOver.add(sorkel)
        }
        lufthavnButtonTeller = false
    }

    private fun fjernLufthavner() {
        if (ferdigsirkelMutableListOver.size > 0) {
            for (sirkali in ferdigsirkelMutableListOver) {
                sirkali.remove()
            }
        }
        else {
            Log.d("fjernLufthavner", "ferdigsirkelMutableListeOver er tom")
        }
        lufthavnButtonTeller = true
    }


}
