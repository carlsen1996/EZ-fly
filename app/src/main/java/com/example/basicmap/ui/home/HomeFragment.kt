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
import com.example.basicmap.lib.getJsonDataFromAsset
import com.example.basicmap.lib.initNoFlyLufthavnSirkel
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.weather.*
import kotlinx.android.synthetic.main.weather.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt


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

        // This is no longer used, so might be removed
        job = Job()

        // Run `onMapReady` when the map is ready to be used.
        // Anything that require the map needs to be there.
        mapFragment.getMapAsync(this)

        // Update the UI when address/weather changes.
        // When `model.place` is set in `onMapClick` the model will automatically
        // fetch the associated address and weather info in the background,
        // notifying us here when done.
        model.address.observe(viewLifecycleOwner, Observer { address ->
            if (address == "")
                popup.locationNameView.text = "Ingen addresseinformasjon tilgjengelig"
            else
                popup.locationNameView.text = address
        })
        model.weather.observe(viewLifecycleOwner, Observer { weather ->
            populatePopup(weather)
        })

        model.astronomicalData.observe(viewLifecycleOwner, Observer { astronomicalData ->
            if (astronomicalData != null)
                populatePopupWithAstroData(astronomicalData)
        })

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        root.lagreLokasjonsKnapp.setOnClickListener {
            // This is only accessible from the popup, meaning the position has been set
            val place = model.getPlace().value!!
            place.address = model.address.value ?: ""
            val places = placesViewModel.getPlaces().value!!
            val toast = Toast.makeText(
                activity,
                "",
                Toast.LENGTH_SHORT
            )
            if (place.favorite) {
                popup.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_off)
                toast.setText("${model.address.value}, er fjernet fra favoritter")
                place.favorite = false
                places.remove(place)
            } else {
                popup.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_on)
                toast.setText("${model.address.value}, er lagt til i favoritter")
                place.favorite = true
                places.add(place)
            }
            placesViewModel.getPlaces().value = places
            toast.show()
        }
        root.gotoButton.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLng(model.getPlace().value!!.position))
        }

        return root
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener(this)

        // Make sure new markers are inside the viewport
        model.getPlace().observe(viewLifecycleOwner, Observer {
            Log.d("place observer", "foo")
            if (it == null)
                return@Observer

            if (it.favorite) {
                popup.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_on)
            } else {
                popup.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_off)
            }

            val oldPlace = marker?.tag as Place?
            if (oldPlace == it) {
                return@Observer
            }

            marker?.remove()
            marker = map.addMarker(MarkerOptions().position(it.position))
            marker?.setTag(it)
            val bounds = map.projection.visibleRegion.latLngBounds
            if (!bounds.contains(it.position)) {
                Log.d("place observer", "move.")
                map.animateCamera(CameraUpdateFactory.newLatLng(it.position))
            }
        })

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
        // Store the location in the view model, it will do the necessary work of
        // fetching weather and address info
        model.getPlace().value = Place(p)
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
            var botview = BottomSheetBehavior.from(popup)
            botview.state = BottomSheetBehavior.STATE_EXPANDED
            popup.visibility = View.VISIBLE
            val weatherIconName = weather.properties.timeseries[0].data.next_1_hours.summary.symbol_code
            val id = resources.getIdentifier(weatherIconName, "mipmap", requireActivity().packageName)
            popup.weatherImageView.setImageResource(id)
            val times = mutableListOf<Met.Numb>()
            val day = listOf("Søndag", "Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag")


            val utc = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"))
            val timeseries = weather.properties.timeseries

            val days = mapOf(
                DayOfWeek.MONDAY to mutableListOf<Met.Numb>(),
                DayOfWeek.TUESDAY to mutableListOf<Met.Numb>(),
                DayOfWeek.WEDNESDAY to mutableListOf<Met.Numb>(),
                DayOfWeek.THURSDAY to mutableListOf<Met.Numb>(),
                DayOfWeek.FRIDAY to mutableListOf<Met.Numb>(),
                DayOfWeek.SATURDAY to mutableListOf<Met.Numb>(),
                DayOfWeek.SUNDAY to mutableListOf<Met.Numb>()
            )

            val idToDays = mapOf(
                R.id.monday to DayOfWeek.MONDAY,
                R.id.tuesday to DayOfWeek.TUESDAY,
                R.id.wednesday to DayOfWeek.WEDNESDAY,
                R.id.thursday to DayOfWeek.THURSDAY,
                R.id.friday to DayOfWeek.FRIDAY,
                R.id.saturday to DayOfWeek.SATURDAY,
                R.id.sunday to DayOfWeek.SUNDAY
            )

            for (data in timeseries) {
                val time = data.time
                val datetime = LocalDateTime.from(utc.parse(time))
                days[datetime.dayOfWeek]?.add(data)
            }
            val now = LocalDateTime.now()


            dayBar.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId < 0)
                    return@setOnCheckedChangeListener

                for (data in days[idToDays.get(checkedId)]!!) {
                    val time = data.time
                    val datetime = LocalDateTime.from(utc.parse(time))

                    if (datetime.hour == 12) {
                        val tempNow = data.data.instant.details.air_temperature?.toDouble()?.roundToInt().toString()
                        popup.precipitationView.text = "NEDBØR\n${data.data.instant.details.fog_area_fraction}%" //regn eller nedbør riktig her?
                        popup.visibilityView.text = "TÅKE\n${data.data.instant.details.fog_area_fraction}%"
                        popup.kpindexView.text = "KP\n3"

                        popup.tempValue.text = "${tempNow}°C"

                    }
                }
            }

            when(now.dayOfWeek) {
                DayOfWeek.MONDAY -> dayBar.check(R.id.monday)
                DayOfWeek.TUESDAY -> dayBar.check(R.id.tuesday)
                DayOfWeek.WEDNESDAY -> dayBar.check(R.id.wednesday)
                DayOfWeek.THURSDAY -> dayBar.check(R.id.thursday)
                DayOfWeek.FRIDAY -> dayBar.check(R.id.friday)
                DayOfWeek.SATURDAY -> dayBar.check(R.id.saturday)
                DayOfWeek.SUNDAY -> dayBar.check(R.id.sunday)
                null -> dayBar.clearCheck()
            }


//            popup.day0.text = "Nå:\n" +
//                    "Vindhastighet: ${weather.properties.timeseries[0].data.instant.details.wind_speed} m/s\n" +
//                    "Max vindkast: ${weather.properties.timeseries[0].data.instant.details.wind_speed_of_gust} m/s\n" +
//                    "Temperatur: ${weather.properties.timeseries[0].data.instant.details.air_temperature} °C\n" +
//                    "Regn: ${weather.properties.timeseries[0].data.next_1_hours.details.precipitation_amount} mm\n" +
//                    "Tåke: ${weather.properties.timeseries[0].data.instant.details.fog_area_fraction}%"
//
//            dayNow++
//            popup.day1.text = "${day[dayNow]}:\n" +
//                    "Vindhastighet: ${times[0].data.instant.details.wind_speed} m/s\n" +
//                    "Max vindkast: ${times[0].data.instant.details.wind_speed_of_gust} m/s\n" +
//                    "Temperatur: ${times[0].data.instant.details.air_temperature} °C\n" +
//                    "Regn: ${times[0].data.next_6_hours.details.precipitation_amount} mm\n" +
//                    "Tåke: ${times[0].data.instant.details.fog_area_fraction}%"
//            dayNow++
//            if (dayNow == 7) {
//                dayNow = 0
//            }
//            popup.day2.text = "${day[dayNow]}:\n" +
//                    "Vindhastighet: ${times[1].data.instant.details.wind_speed} m/s\n" +
//                    "Max vindkast: ${times[1].data.instant.details.wind_speed_of_gust} m/s\n" +
//                    "Temperatur: ${times[1].data.instant.details.air_temperature} °C\n" +
//                    "Regn: ${times[1].data.next_6_hours.details.precipitation_amount} mm\n" +
//                    "Tåke: ${times[1].data.instant.details.fog_area_fraction}%"
//            dayNow++
//            if (dayNow == 7) {
//                dayNow = 0
//            }
//            if (times[2].data.next_6_hours.details.precipitation_amount == null) {
//                popup.day3.text = "${day[dayNow]}\n" +
//                        "Vindhastighet: ${times[2].data.instant.details.wind_speed} m/s\n" +
//                        "Temperatur: ${times[2].data.instant.details.air_temperature} °C\n" +
//                        "Regn: ${times[2].data.next_1_hours.details.precipitation_amount} mm\n"
//
//            } else {
//                popup.day3.text = "${day[dayNow]}:\n" +
//                        "Vindhastighet: ${times[2].data.instant.details.wind_speed} m/s\n" +
//                        "Temperatur: ${times[2].data.instant.details.air_temperature} °C\n" +
//                        "Regn: ${times[2].data.next_6_hours.details.precipitation_amount} mm\n"
//            }
//            dayNow++
//            if (dayNow == 7) {
//                dayNow = 0
//            }
//            if (times[3].data.next_6_hours.details.precipitation_amount == null) {
//                popup.day4.text = "${day[dayNow]}:\n" +
//                        "Vindhastighet: ${times[3].data.instant.details.wind_speed} m/s\n" +
//                        "Temperatur: ${times[3].data.instant.details.air_temperature} °C\n" +
//                        "Regn: ${times[3].data.next_1_hours.details.precipitation_amount} mm\n"
//            } else {
//                popup.day4.text = "${day[dayNow]}:\n" +
//                        "Vindhastighet: ${times[3].data.instant.details.wind_speed} m/s\n" +
//                        "Temperatur: ${times[3].data.instant.details.air_temperature} °C\n" +
//                        "Regn: ${times[3].data.next_6_hours.details.precipitation_amount} mm\n"
//            }
//
//            dayNow++
//            if (dayNow == 7) {
//                dayNow = 0
//            }
//            if (times[4].data.next_6_hours.details.precipitation_amount == null) {
//                popup.day5.text = "${day[dayNow]}:\n" +
//                        "Vindhastighet: ${times[4].data.instant.details.wind_speed} m/s\n" +
//                        "Temperatur: ${times[4].data.instant.details.air_temperature} °C\n" +
//                        "Regn: ${times[4].data.next_1_hours.details.precipitation_amount} mm\n"
//            } else {
//                popup.day5.text = "${day[dayNow]}:\n" +
//                        "Vindhastighet: ${times[4].data.instant.details.wind_speed} m/s\n" +
//                        "Temperatur: ${times[4].data.instant.details.air_temperature} °C\n" +
//                        "Regn: ${times[4].data.next_6_hours.details.precipitation_amount} mm\n"
//            }

        }
    }

    fun populatePopupWithAstroData(astroData: Met.AstronomicalData) {

        activity?.runOnUiThread{
            //popup.sunSetTimeView.text = "Solnedgang: ${astroData.sunset}"
            //popup.sunRiseTimeView.text = "Soloppgang: ${astroData.sunrise}"
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
