package com.example.basicmap.ui.drones

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.basicmap.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_drones.view.*

class DronesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var dronesViewModel: DronesViewModel
    var droneList =  mutableListOf<Drone>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dronesViewModel =
            ViewModelProviders.of(this).get(DronesViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_drones, container, false)

        loadData()

        //Add drone
        root.registrerKnapp.setOnClickListener {
            val intent = Intent(getActivity(), RegistrerDrone::class.java)
            startActivityForResult(intent, 1)
        }

        //Fill recyclerview
        viewManager = LinearLayoutManager(getActivity())
        viewAdapter = ListAdapter(getContext()!!, droneList)

        recyclerView = root.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }

    private fun loadData() {
        val sharedPref: SharedPreferences = activity!!.getSharedPreferences("sharedPref", AppCompatActivity.MODE_PRIVATE)
        val gson = GsonBuilder().create()
        val json = sharedPref.getString("droneList", null)
        val drones = gson.fromJson(json, Array<Drone>::class.java)
        if(drones == null) {
            return
        }
        droneList = drones.toMutableList()
    }

}