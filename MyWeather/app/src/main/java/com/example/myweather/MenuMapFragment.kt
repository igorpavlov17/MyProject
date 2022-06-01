package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class MenuMapFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu_map, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ConstraintLayout>(R.id.temp_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment.newInstance("temp_new")).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.precipitation_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment.newInstance("precipitation_new")).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.clouds_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment.newInstance("clouds_new")).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.pressure_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment.newInstance("pressure_new")).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.wind_map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MapFragment.newInstance("wind_new")).addToBackStack(null).commit()
        }
    }
}