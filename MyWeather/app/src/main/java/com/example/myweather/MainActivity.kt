package com.example.myweather

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val city: String = "Рыбинск"
    private val api = "34dc93bfdf3425debd0c37b6580d8fe0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().add(R.id.main_container, WeatherFragment(city, api)).commit()
        //supportFragmentManager.beginTransaction().add(R.id.main_container, ForecastFragment(api)).commit()

        findViewById<ImageView>(R.id.edit_city).setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.main_container, EditCityFragment(api)).addToBackStack(null).commit()
        }

        findViewById<ImageView>(R.id.show_info).setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.main_container, InfoFragment()).addToBackStack(null).commit()
        }

        findViewById<ImageView>(R.id.show_menu).setOnClickListener {
            supportFragmentManager.beginTransaction().setCustomAnimations(
                R.animator.to_left_in,
                R.animator.to_left_out,
                R.animator.to_right_in,
                R.animator.to_right_out
            ).add(R.id.main_container, MenuFragment(api)).addToBackStack(null).commit()
        }
    }
}