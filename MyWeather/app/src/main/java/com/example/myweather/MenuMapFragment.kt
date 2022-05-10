package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide

class MenuMapFragment(private val api: String) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu_map, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ConstraintLayout>(R.id.temp_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment("temp_new", api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.precipitation_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment("precipitation_new", api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.clouds_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment("clouds_new", api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.pressure_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment("pressure_new", api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.wind_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment("wind_new", api)).addToBackStack(null).commit()
        }
    }
}