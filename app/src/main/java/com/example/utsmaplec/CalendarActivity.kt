package com.example.utsmaplec

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class CalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)

        // Bottom Navigation View setup
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Pindah ke HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_calendar -> {
                    // Sudah di CalendarActivity, tidak perlu start ulang
                    true
                }
                R.id.nav_profile -> {
                    // Pindah ke ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Set default selected item to calendar
        bottomNavigationView.selectedItemId = R.id.nav_calendar
    }
}
