package com.example.basicmap.ui.info

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import com.example.basicmap.R
import com.mahfa.dnswitch.DayNightSwitch
import com.mahfa.dnswitch.DayNightSwitchListener
import kotlinx.android.synthetic.main.fragment_info.view.*


class InfoFragment : Fragment() {
    val questions = listOf(
        "Les dette før du flyr",
        "Kan jeg fly dronen min i minusgrader?",
        "Hva hvis det regner/snør?",
        "Hvor høyt kan jeg fly?",
        "Kan jeg fly om kvelden?",
        "Hvor finner jeg lover om droneflyging i Norge?",
        "Obs Nytt regelverk",
        "Produsert av: ",
        "En spesiell takk til: "
    )
    val answers = listOf(
        listOf("Det er sterkt anbefalt at alle brukere leser seg opp på dronelovene i Norge.\n\n" +
                "Det er veldig mange regler som handler om hvor man kan fly og ikke fly. " +
                "Appen viser hovedsakelig No-Fly sonene rundt lufthavnene, men det er mange lokale regler man må passe på. \n" +
                "Noen ting å passe på er Natur-reservater, festivaler, sportsarrangementer, skoler, sykehus og noen offentlige områder \n\n" +
                "Lykke til!"),
        listOf("Det er ikke anbefalt, men mulig. Batteriet mister raskt " +
                "kapasitet i kulden. Pass på at dronen din er ladet" +
                ". Et tips er å bruke håndvarmere for å " +
                "holde batteriet varmt."),
        listOf("Det kommer an på dronen din. De fleste droner er ikke vanntette, så " +
                "her bør du sjekke din modell spesifikt."),
        listOf("Luftfartstilsynet sin regel er ikke høyere enn 120m over bakken."),
        listOf("Hovedregelen er at du alltid må kunne se dronen når den flys. Dersom " +
                "det er mørkt og du har dårlig sikt, skal du ikke fly."),
        listOf("Du kan finne hovedreglene her : \n\n" +
                "https://luftfartstilsynet.no/droner/hobbydrone/dronelek/\n\n" +
                "Fullt regelverk her : \n\n"+
                "https://lovdata.no/dokument/SF/forskrift/2015-11-30-1404\n"),
        listOf("Fra og med 1 Januar 2021 så vil det nye EU regelverket for droner tre i kraft i Norge."+
                " Vi anbefaler brukere å lese seg opp på det nye regelverket før det trer i kraft."),
        listOf("Al Hareth Adel\n"+
                "Johannes Carlsen\n"+
                "Olav Moen\n"+
                "Philip A.K. Øvland\n"+
                "Rolf Selås\n"+
                "Tor Hedin Brønner\n"),
        listOf("Daniel Hellestveit\n"+
                "Ove Bakken\n"+
                "Lossius Solutions\n"+
                "Raymond Andreassen Rainmanproductions\n"+
                "Bård Brattvoll\n"+
                "Atle Kristian Kolbeinsen\n"+
                "Marius E Johansen\n"+
                "Morten Obbink\n"+
                "Andreas Gjetanger\n"+
                "Torgrim Kiil\n"+
                "Sanosh Senthilkumar\n"+
                "Trond Glesaaen\n"+
                "Kim Johnsen\n"
                )

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
            root.faqimg.setImageResource(R.mipmap.faq)
        }
        else {
            switch.setIsNight(userPreferenceDarkMode)
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