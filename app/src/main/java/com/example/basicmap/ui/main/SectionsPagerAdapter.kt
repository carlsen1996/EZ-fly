package com.example.basicmap.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.basicmap.R
import com.example.basicmap.ui.drones.DronesFragment
import com.example.basicmap.ui.faq.FaqFragment
import com.example.basicmap.ui.home.HomeFragment
import com.example.basicmap.ui.places.PlacesFragment

private val TAB_TITLES = arrayOf(
    R.string.title_home,
    R.string.title_places,
    R.string.title_drones,
    R.string.title_faq
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.

        return when (position) {
            0 -> HomeFragment()
            1 -> PlacesFragment()
            2 -> DronesFragment()
            3 -> FaqFragment()
            else -> Fragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}