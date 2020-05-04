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

        loadData()

        placesViewModel.getPlaces().observe(viewLifecycleOwner, Observer<MutableList<Place>> {
            viewManager = LinearLayoutManager(activity)
            viewAdapter = PlacesListAdapter(this, placesViewModel.getPlaces().value)
            recyclerView = root.recyclerView.apply {


                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
            if(viewAdapter.itemCount == 0) {
                recycleViewTekstPlaces.visibility = VISIBLE
            }
            else {
                recycleViewTekstPlaces.visibility = INVISIBLE
            }
            saveData()
        })

        return root
    }

    private fun loadData() {
        val sharedPrefPlaces: SharedPreferences = requireActivity().getSharedPreferences("sharedPrefPlaces", AppCompatActivity.MODE_PRIVATE)
        val gson = GsonBuilder().create()
        val json = sharedPrefPlaces.getString("placesList", null)
        val places = gson.fromJson(json, Array<Place>::class.java) ?: return
        Log.d("placeslist", json)
        placesViewModel.getPlaces().value = places.toMutableList()
    }

    private fun saveData() {
        Log.d("saveData", "")
        val sharedPref: SharedPreferences = requireActivity().getSharedPreferences("sharedPrefPlaces", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = GsonBuilder().create()
        val json = gson.toJson(placesViewModel.getPlaces().value)
        editor.putString("placesList", json)
        editor.apply()
    }
}
