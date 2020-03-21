package com.example.basicmap.ui.drones

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.basicmap.R
import kotlinx.android.synthetic.main.fragment_drones.view.*

class DronesFragment : Fragment() {

    private lateinit var dronesViewModel: DronesViewModel
    var droneList = arrayListOf<Drone>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dronesViewModel =
            ViewModelProviders.of(this).get(DronesViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_drones, container, false)

        root.registrerKnapp.setOnClickListener {
            val intent = Intent(getActivity(), RegistrerDrone::class.java)
            startActivityForResult(intent, 1)
        }
        return root
    }

}