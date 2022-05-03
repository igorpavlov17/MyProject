package com.example.myweather

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.main_container, WeatherFragment()).commit()

        findViewById<ImageView>(R.id.show_info).setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.main_container, InfoFragment()).addToBackStack(null).commit()
        }
        findViewById<ImageView>(R.id.show_menu).setOnClickListener {
            supportFragmentManager.beginTransaction().setCustomAnimations(
                R.animator.to_left_in,
                R.animator.to_left_out,
                R.animator.to_right_in,
                R.animator.to_right_out
            ).add(R.id.main_container, MenuFragment()).commit()
        }
    }
}