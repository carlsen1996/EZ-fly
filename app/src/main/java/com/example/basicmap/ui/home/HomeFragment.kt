package com.example.basicmap.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.basicmap.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class HomeFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private val positions = mutableListOf<LatLng>()
    private var polygon: Polygon? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val map = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)
    }

    override fun onMapClick(p0: LatLng?) {
        if (p0 == null)
            return

        marker?.remove()
        marker = mMap.addMarker(MarkerOptions().position(p0))
        positions.add(p0)

        // This is a bit ugly, and should really only be in a constructor of some sorts,
        // but creating an empty polygon doesn't seem to work.
        // So we delay it until we have at least on point
        if (polygon == null)
            polygon = mMap.addPolygon(PolygonOptions().addAll(positions))

        polygon?.points = positions
    }
}