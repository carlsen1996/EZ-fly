package com.example.basicmap.ui.drones

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dronesViewModel = DronesViewModel()
        val root = inflater.inflate(R.layout.fragment_drones, container, false)
        loadData()

        //Fill recyclerview
        dronesViewModel.getDroneList().observe(viewLifecycleOwner, Observer<MutableList<Drone>> {
            viewManager = LinearLayoutManager(activity)
            viewAdapter = ListAdapter(context!!, dronesViewModel.getDroneList().value)
            recyclerView = root.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
                saveData()
            }
            if(viewAdapter.itemCount == 0) {
                root.recycleViewTekst.visibility = VISIBLE
            }
            saveData()
        })

        //Add drone
        root.registrerKnapp.setOnClickListener {
            root.recycleViewTekst.visibility = INVISIBLE
            val intent = Intent(activity, RegistrerDrone::class.java)
            startActivityForResult(intent, 1)
        }

        return root
    }

    //Hent drone liste fra minne
    private fun loadData() {
        val sharedPref: SharedPreferences = activity!!.getSharedPreferences("sharedPref", AppCompatActivity.MODE_PRIVATE)
        val gson = GsonBuilder().create()
        val json = sharedPref.getString("droneList", null)
        val drones = gson.fromJson(json, Array<Drone>::class.java) ?: return
        dronesViewModel.getDroneList().value = drones.toMutableList()
    }
    //Lagre drone liste i minne
    private fun saveData() {
        val sharedPref: SharedPreferences = activity!!.getSharedPreferences("sharedPref", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = GsonBuilder().create()
        val json = gson.toJson(dronesViewModel.getDroneList().value)
        editor.putString("droneList", json)
        editor.apply()
    }

}

