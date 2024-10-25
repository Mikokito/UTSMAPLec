package com.example.utsmaplec

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(navListener)

        // Load default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.nav_host_fragment,
                HomeFragment()
            ).commit()
        }
    }

    private val navListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_home -> supportFragmentManager.beginTransaction().replace(
                    R.id.nav_host_fragment,
                    HomeFragment()
                ).commit()

                R.id.nav_calendar -> supportFragmentManager.beginTransaction().replace(
                    R.id.nav_host_fragment,
                    CalendarFragment()
                ).commit()

                R.id.nav_alarm -> supportFragmentManager.beginTransaction().replace(
                    R.id.nav_host_fragment,
                    AlarmFragment()
                ).commit()

                R.id.nav_profile -> supportFragmentManager.beginTransaction().replace(
                    R.id.nav_host_fragment,
                    ProfileFragment()
                ).commit()
            }
            true
        }
}
