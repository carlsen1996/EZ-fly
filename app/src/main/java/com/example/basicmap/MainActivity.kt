package com.example.basicmap

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.basicmap.ui.main.SectionsPagerAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)

        tabs.setupWithViewPager(viewPager)

        // This is a bit ugly, but should work
        // FIXME: icon sizes are wrong
        tabs.getTabAt(0)?.setIcon(R.mipmap.round_place_black_18dp)
        tabs.getTabAt(1)?.setIcon(R.mipmap.round_explore_black_18dp)
        tabs.getTabAt(2)?.setIcon(R.mipmap.drone4)
        tabs.getTabAt(3)?.setIcon(R.mipmap.round_help_black_18dp)
    }
}