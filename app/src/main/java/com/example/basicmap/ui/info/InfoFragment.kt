package com.example.basicmap.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.basicmap.R
import com.mahfa.dnswitch.DayNightSwitch
import com.mahfa.dnswitch.DayNightSwitchListener
import kotlinx.android.synthetic.main.fragment_info.view.*


class InfoFragment : Fragment() {
    val questions = listOf(
        "Kan jeg fly dronen min i minusgrader?",
        "Hva hvis det regner/snør?",
        "Hvor høyt kan jeg fly?",
        "Kan jeg fly om kvelden?"
    )
    val answers = listOf(
        listOf("Det er ikke anbefalt, men mulig. Batteriet mister raskt " +
                "kapasitet i kulden. Pass på at dronen din er ladet" +
                ". Et tips er å bruke håndvarmere for å " +
                "holde batteriet varmt."),
        listOf("Det kommer an på dronen din. De fleste droner er ikke vanntette, så " +
                "her bør du sjekke din modell spesifikt."),
        listOf("Luftfartstilsynet sin regel er ikke høyere enn 120m over bakken."),
        listOf("Hovedregelen er at du alltid må kunne se dronen når den flys. Dersom " +
                "det er mørkt og du har dårlig sikt, skal du ikke fly.")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_info, container, false)

        val adapter = FaqListAdapter(requireContext(), questions, answers)
        root.faqListView.setAdapter(adapter)

        var switch : DayNightSwitch = root.darkSwitch
        val preferenceDarkMode = DarkPref(requireContext())
        var userPreferenceDarkMode = preferenceDarkMode.getDarkPref()
        if (userPreferenceDarkMode) {
            switch.setIsNight(userPreferenceDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            root.faqimg.setImageResource(R.mipmap.faq)
        }
        else {
            switch.setIsNight(userPreferenceDarkMode)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            root.faqimg.setImageResource(R.mipmap.faq2)
        }


        switch.setListener(DayNightSwitchListener {
            if (it) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                root.faqimg.setImageResource(R.mipmap.faq)
                preferenceDarkMode.setDarkPref(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                root.faqimg.setImageResource(R.mipmap.faq2)
                preferenceDarkMode.setDarkPref(false)
            }
        })


        return root
    }
}