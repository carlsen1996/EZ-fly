package com.example.basicmap.ui.faq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import androidx.fragment.app.Fragment
import com.example.basicmap.R
import kotlinx.android.synthetic.main.fragment_faq.view.*


class FaqFragment : Fragment() {
    val question : MutableList<String> = mutableListOf()
    val answer : MutableList<MutableList<String>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val answer1 : MutableList<String> = mutableListOf()
        val answer2 : MutableList<String> = mutableListOf()
        val answer3 : MutableList<String> = mutableListOf()
        val answer4 : MutableList<String> = mutableListOf()

        question.add("Kan jeg fly dronen min i minusgrader?")
        answer1.add("Det er ikke anbefalt, men mulig. Batteriet mister raskt " +
                "kapasitet i kulden. Pass på at dronen din er ladet" +
                ". Et tips er å bruke håndvarmere for å " +
                "holde batteriet varmt.")
        question.add("Hva hvis det regner/snør?")
        answer2.add("Det kommer an på dronen din. De fleste droner er ikke vanntette, så " +
                "her bør du sjekke din modell spesifikt.")
        question.add("Hvor høyt kan jeg fly?")
        answer3.add("Luftfartstilsynet sin regel er ikke høyere enn 120m over bakken.")
        question.add("Kan jeg fly om kvelden?")
        answer4.add("Hovedregelen er at du alltid må kunne se dronen når den flys. Dersom " +
                "det er mørkt og du har dårlig sikt, skal du ikke fly.")

        answer.add(answer1)
        answer.add(answer2)
        answer.add(answer3)
        answer.add(answer4)

        val root = inflater.inflate(R.layout.fragment_faq, container, false)

        val adapter = FaqListAdapter(context!!, question, answer)
        root.faqListView.setAdapter(adapter)
        return root
    }
}