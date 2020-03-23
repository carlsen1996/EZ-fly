package com.example.basicmap.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.basicmap.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_places.view.*

class PlacesFragment : Fragment() {

    private lateinit var tripsViewModel: TripsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        tripsViewModel =
            ViewModelProviders.of(this).get(TripsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_places, container, false)

        val places = mutableListOf<LatLng>(
            LatLng(59.94195862876364, 10.76321694999933),
            LatLng(59.94377610821358, 10.762283876538277),
            LatLng(59.943641097920406, 10.759101770818233),
            LatLng(59.942160986342856, 10.754415281116962)
        )

        root.placesView.adapter = PlacesList(places)
        root.placesView.layoutManager = LinearLayoutManager(context)

        return root
    }
}
