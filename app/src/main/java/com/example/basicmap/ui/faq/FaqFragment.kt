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

        question.add("Question 1")
        answer1.add("Answer 1")
        question.add("Question 2")
        answer2.add("Answer 2")
        question.add("Question 3")
        answer3.add("Answer 3")
        question.add("Question 4")
        answer4.add("Answer 4")

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