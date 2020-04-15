package com.example.basicmap

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.example.basicmap.ui.drones.DronesFragment
import com.example.basicmap.ui.info.InfoFragment
import com.example.basicmap.ui.home.HomeFragment
import com.example.basicmap.ui.info.DarkPref
import com.example.basicmap.ui.places.PlacesFragment

private val TAB_TITLES = arrayOf(
    R.string.title_home,
    R.string.title_places,
    R.string.title_drones,
    R.string.title_info
)

class MainActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        // While this is deprecated, ViewPager2 introduces a bunch of unnecessary complications
        // with scrolling.
        viewPager.adapter = object: FragmentPagerAdapter(supportFragmentManager) {
            // getItem is called to instantiate the fragment for the given page.
            override fun getItem(position: Int): Fragment = when (position) {
                0 -> HomeFragment()
                1 -> PlacesFragment()
                2 -> DronesFragment()
                3 -> InfoFragment()
                else -> Fragment()
            }
            override fun getPageTitle(position: Int): CharSequence? = getString(TAB_TITLES[position])
            override fun getCount(): Int = TAB_TITLES.size
        }

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        // This is a bit ugly, but should work
        // FIXME: icon sizes are wrong
        tabs.getTabAt(0)?.setIcon(R.mipmap.round_place_black_18dp)
        tabs.getTabAt(1)?.setIcon(R.mipmap.round_explore_black_18dp)
        tabs.getTabAt(2)?.setIcon(R.mipmap.drone4)
        tabs.getTabAt(3)?.setIcon(R.mipmap.round_help_black_18dp)


        val preferenceDarkMode = DarkPref(this)
        var userPreferenceDarkMode = preferenceDarkMode.getDarkPref()
        if (userPreferenceDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

    }
}