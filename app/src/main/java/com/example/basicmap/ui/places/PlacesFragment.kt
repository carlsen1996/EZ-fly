package com.example.basicmap.ui.places

import PlacesListAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R
import com.example.basicmap.lib.loadPlaces
import com.example.basicmap.lib.savePlaces
import com.example.basicmap.ui.home.HomeViewModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_places.*
import kotlinx.android.synthetic.main.fragment_places.view.*

class PlacesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val placesViewModel = PlacesViewModel()
    val homeViewModel: HomeViewModel by viewModels()
    var placesList =  mutableListOf<Place>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_places, container, false)

        placesViewModel.getPlaces().value = placesViewModel.getPlaces().value
            ?: loadPlaces(requireContext())
        viewManager = LinearLayoutManager(activity)
        viewAdapter = PlacesListAdapter(this, placesViewModel.getPlaces().value)
        recyclerView = root.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        placesViewModel.getPlaces().observe(viewLifecycleOwner, Observer {
            viewAdapter.notifyDataSetChanged()
            if(viewAdapter.itemCount == 0) {
                recycleViewTekstPlaces.visibility = VISIBLE
            }
            else {
                recycleViewTekstPlaces.visibility = INVISIBLE
            }
            savePlaces(requireContext(), it)
        })

        return root
    }

}
