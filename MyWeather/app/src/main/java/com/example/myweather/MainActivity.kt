package com.example.myweather

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myweather.db.MyDbManager
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var myDbManager: MyDbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        myDbManager = MyDbManager(this)
        myDbManager.openDB()

        findViewById<TextView>(R.id.address)?.text = myDbManager.getContentByTitle("address")

        if (findViewById<TextView>(R.id.address)?.text == "") {
            myDbManager.insertToDb("temp_unit", "metric", "")
            myDbManager.insertToDb("wind_unit", "ms", "")
            myDbManager.insertToDb("pressure_unit", "mmrtst", "")
            findViewById<ConstraintLayout>(R.id.address_container).visibility = View.GONE
            supportFragmentManager.beginTransaction().replace(R.id.main_container, EditCityFragment()).commit()
        }
        else if (savedInstanceState == null) supportFragmentManager.beginTransaction().replace(R.id.main_container, WeatherFragment()).commit()


        findViewById<TextView>(R.id.time)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())

        findViewById<ImageView>(R.id.edit_city).setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.main_container, EditCityFragment()).addToBackStack(null).commit()
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
            ).add(R.id.main_container, MenuFragment()).addToBackStack(null).commit()
        }
    }
}