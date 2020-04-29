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
import com.example.basicmap.lib.saveDrones
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_drones.view.*

class DronesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var dronesViewModel = DronesViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_drones, container, false)

        //Fill recyclerview
        dronesViewModel.getDroneList().observe(viewLifecycleOwner, Observer<MutableList<Drone>> {
            viewManager = LinearLayoutManager(activity)
            viewAdapter = ListAdapter(requireContext(), dronesViewModel.getDroneList().value)
            recyclerView = root.recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
            if(viewAdapter.itemCount == 0) {
                root.recycleViewTekst.visibility = VISIBLE
            }
            saveDrones(requireContext(), it)
        })

        //Add drone
        root.registrerKnapp.setOnClickListener {
            root.recycleViewTekst.visibility = INVISIBLE
            val intent = Intent(activity, RegistrerDrone::class.java)
            startActivity(intent)
        }

        return root
    }
}

