package com.example.basicmap.ui.places

import PlacesListAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_drones.view.recyclerView
import kotlinx.android.synthetic.main.fragment_places.view.*

class PlacesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val placesViewModel: PlacesViewModel by viewModels()
    var placesList =  mutableListOf<Place>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_places, container, false)

        //loadData()


        //Fill recyclerview
        viewManager = LinearLayoutManager(activity)
        viewAdapter = PlacesListAdapter(requireContext(), placesList)
        recyclerView = root.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter

        }

        if(viewAdapter.getItemCount() == 0) {
            root.recycleViewTekstPlaces.setVisibility(VISIBLE)
        }

        return root
    }

    private fun loadData() {
        val sharedPrefPlaces: SharedPreferences = requireActivity().getSharedPreferences("sharedPrefPlaces", AppCompatActivity.MODE_PRIVATE)
        val gson = GsonBuilder().create()
        val json = sharedPrefPlaces.getString("placesList", null)
        val places = gson.fromJson(json, Array<Place>::class.java)
        if(places == null) {
            return
        }
        placesList = places.toMutableList()
    }

    //Update recyclerview
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            //loadData()
            viewManager = LinearLayoutManager(getActivity())
            viewAdapter = PlacesListAdapter(requireContext(), placesList)
            recyclerView = recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        }
    }

}
