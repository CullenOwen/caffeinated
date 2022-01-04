package com.example.caffeinated

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment

var current_fragment_id: String = "Home"
lateinit var fragmentToLoad: Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (current_fragment_id == "Automated") {
            fragmentToLoad = AutomatedAdd()
        } else if (current_fragment_id == "Manual") {
            fragmentToLoad = ManualAdd()
        } else if (current_fragment_id == "History") {
            fragmentToLoad = CaffeineHistoryFragment()
        } else {
            fragmentToLoad = HomeFragment()
        }

        Log.d("HELP", current_fragment_id)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_body, fragmentToLoad)
            commit()
        }


        var bottom_nav_menu = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_nav_menu.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_home -> {
                    Log.d("DEBUG", "Home action")
                    fragmentToLoad = HomeFragment()
                    current_fragment_id = "Home"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_body, fragmentToLoad)
                        commit()
                    }
                }
                R.id.action_manual_add -> {
                    Log.d("DEBUG", "Manual action")
                    fragmentToLoad = ManualAdd()
                    current_fragment_id = "Manual"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_body, fragmentToLoad)
                        commit()
                    }
                }
                R.id.action_automated_add -> {
                    Log.d("DEBUG", "Automated action applied")
                    fragmentToLoad = AutomatedAdd()
                    current_fragment_id = "Automated"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_body, fragmentToLoad)
                        commit()
                    }
                }
                R.id.action_caffeine_history -> {
                    Log.d("DEBUG", "Caffeine History Action")
                    fragmentToLoad = CaffeineHistoryFragment()
                    current_fragment_id = "History"
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frame_body, fragmentToLoad)
                        commit()
                    }
                }
            }
            true
        }
    }
}

/*
*
* TABLE:
* Fields: UserID, Name_Of_Product, BARCODE? (can be null?), Number_Consumed, Date_Consumed, Amount_Of_Caffeine
*
* */