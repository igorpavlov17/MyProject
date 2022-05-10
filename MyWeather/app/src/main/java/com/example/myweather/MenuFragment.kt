package com.example.myweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MenuFragment(private val api: String) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ConstraintLayout>(R.id.weather).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, WeatherFragment(requireActivity().findViewById<TextView>(R.id.address).text.toString(), api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.forecast).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, ForecastFragment(api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.map).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, MenuMapFragment(api)).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.settings).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, SettingsFragment()).addToBackStack(null).commit()
        }
        view.findViewById<ConstraintLayout>(R.id.info).setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_container, InfoFragment()).addToBackStack(null).commit()
        }
    }
}