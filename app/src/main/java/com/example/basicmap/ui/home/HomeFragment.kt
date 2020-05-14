package com.example.basicmap.ui.home

import android.Manifest
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
import com.example.basicmap.lib.getJsonDataFromAsset
import com.example.basicmap.lib.initNoFlyLufthavnSirkel
import com.example.basicmap.lib.setupWeatherElement
import com.example.basicmap.ui.places.LivePlace
import com.example.basicmap.ui.places.Place
import com.example.basicmap.ui.places.PlacesViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.popup.*
import kotlinx.android.synthetic.main.popup.view.*
import kotlinx.android.synthetic.main.weather.view.*
import kotlinx.coroutines.newFixedThreadPoolContext
import com.google.android.libraries.places.api.model.Place as GPlace


private val TAG = "HomeFragment"

class HomeFragment : Fragment(), OnMapReadyCallback, PlaceSelectionListener {

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

        // Run `onMapReady` when the map is ready to be used.
        // Anything that require the map needs to be there.
        mapFragment.getMapAsync(this)

        // Update the UI when address/weather changes.
        // When `model.place` is set in `onMapClick` the model will automatically
        // fetch the associated address and weather info in the background,
        // notifying us here when done.

        setupWeatherElement(requireContext(), viewLifecycleOwner, model.getPlace(), root.popup)

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        root.lagreLokasjonsKnapp.setOnClickListener {
            // This is only accessible from the popup, meaning the position has been set
            val livePlace = model.getPlace()
            val place = livePlace.place.value!!
            val liveAddress = livePlace.address
            place.address = liveAddress.value ?: ""
            val places = placesViewModel.getPlaces().value!!
            val toast = Toast.makeText(
                activity,
                "",
                Toast.LENGTH_SHORT
            )
            if (place.favorite) {
                popup.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_off)
                toast.setText("${liveAddress.value}, er fjernet fra favoritter")
                place.favorite = false
                places.removeIf {
                    it.place.value == place
                }
            } else {
                popup.lagreLokasjonsKnapp.setImageResource(android.R.drawable.star_big_on)
                toast.setText("${liveAddress.value}, er lagt til i favoritter")
                place.favorite = true
                val savedPlace = LivePlace(requireContext())
                savedPlace.place.value = place
                places.add(savedPlace)
            }
            placesViewModel.getPlaces().value = places
            toast.show()
        }
        root.gotoButton.setOnClickListener {
            map.animateCamera(CameraUpdateFactory.newLatLng(model.getPlace().place.value!!.position))
        }

        // Setup search
        val autocompleteFragment = childFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        // The information we're interested in
        autocompleteFragment.setPlaceFields(listOf(GPlace.Field.ID, GPlace.Field.NAME, GPlace.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(this)

        return root
    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.setOnMapClickListener {
            if (it == null)
                return@setOnMapClickListener
            // Store the location in the view model, it will do the necessary work of
            // fetching weather and address info
            model.getPlace().place.value = Place(it)
            moveCameraIfOutsideVisibleRegion(it)
            //moves camera to point if it isn't on the top half of the screen

            //moveCameraIfOutsideVisibleRegionTotal(it)

        }

        map.setOnMarkerClickListener {
            val botview = BottomSheetBehavior.from(popup)
            botview.state = BottomSheetBehavior.STATE_EXPANDED
            true
        }

        // Make sure new markers are inside the viewport
        model.getPlace().place.observe(viewLifecycleOwner, Observer {
            Log.d("place observer", "foo")
            if (it == null)
                return@Observer

            val botview = BottomSheetBehavior.from(popup)
            if (popup.visibility != View.VISIBLE) {
                popup.visibility = View.VISIBLE
                botview.state = BottomSheetBehavior.STATE_EXPANDED
            }

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

    private fun moveCameraIfOutsideVisibleRegionTotal(stedet : LatLng) {
        if(!map.projection.visibleRegion.latLngBounds.contains(stedet)) {
            moveCameraToCaPoint(stedet)
        }
    }

    private fun moveCameraIfOutsideVisibleRegion(stedet : LatLng) {
        val sted = stedet

        val northEast = map.projection.visibleRegion.farRight
        val southEast = map.projection.visibleRegion.nearRight
        val northWest = map.projection.visibleRegion.farLeft

        val nELat = northEast.latitude
        val sELat = southEast.latitude
        val nWLng = northWest.longitude
        val nELng = northEast.longitude

        val latDiff = nELat - sELat
        val nySLat = northEast.latitude - latDiff/2
        val stedLat = sted.latitude
        val stedLng = sted.longitude

        if (stedLat > nELat || stedLat < nySLat || stedLng < nWLng || stedLng > nELng) {
            //problems arise if the visible region is over 180th meridian
            //Tuveuni, Fiji is a place where such a bug might arise
            moveCameraToCaPoint(sted)
        }
    }

    private fun moveCameraToCaPoint(omraade : LatLng) {
        val kar = map.cameraPosition.zoom
        var sted = omraade
        var stedlat = sted.latitude
        if (kar <= 15) {
            stedlat = stedlat-0.002
        }
        else if (kar < 5) {
            stedlat = stedlat-0.02
        }
        //need to adjust the change acordingly to the zoom level

        val stedlong = omraade.longitude
        sted = LatLng(stedlat, stedlong)
        map.animateCamera(CameraUpdateFactory.newLatLng(sted))
        //Log.d("kar", kar.toString())
    }


    override fun onPlaceSelected(place: GPlace) {
        Log.i("place selected", "${place.name}")
        val position = place.latLng!!
        model.getPlace().place.value = Place(position)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
            position, 15.0f
        ))
    }

    override fun onError(status: Status) {
        Log.i("place error", "${status}")
    }
}
