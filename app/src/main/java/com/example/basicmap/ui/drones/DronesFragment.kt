package com.example.basicmap.ui.drones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.basicmap.R

class DronesFragment : Fragment() {

    private lateinit var dronesViewModel: DronesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dronesViewModel =
            ViewModelProviders.of(this).get(DronesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_drones, container, false)
        return root
    }
}