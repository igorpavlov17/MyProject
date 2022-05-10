package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class SettingsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ConstraintLayout>(R.id.temp_unit).setOnClickListener {
            if (view.findViewById<ConstraintLayout>(R.id.temp_unit_setting).visibility == View.GONE)
                view.findViewById<ConstraintLayout>(R.id.temp_unit_setting).visibility = View.VISIBLE
            else view.findViewById<ConstraintLayout>(R.id.temp_unit_setting).visibility = View.GONE
        }
        view.findViewById<ConstraintLayout>(R.id.wind_unit).setOnClickListener {
            if (view.findViewById<ConstraintLayout>(R.id.wind_unit_setting).visibility == View.GONE)
                view.findViewById<ConstraintLayout>(R.id.wind_unit_setting).visibility = View.VISIBLE
            else view.findViewById<ConstraintLayout>(R.id.wind_unit_setting).visibility = View.GONE
        }
        view.findViewById<ConstraintLayout>(R.id.pressure_unit).setOnClickListener {
            if (view.findViewById<ConstraintLayout>(R.id.pressure_unit_setting).visibility == View.GONE)
                view.findViewById<ConstraintLayout>(R.id.pressure_unit_setting).visibility = View.VISIBLE
            else view.findViewById<ConstraintLayout>(R.id.pressure_unit_setting).visibility = View.GONE
        }

        view.findViewById<Button>(R.id.apply).setOnClickListener {
            when (view.findViewById<RadioGroup>(R.id.temp_unit_group).checkedRadioButtonId) {
                view.findViewById<RadioButton>(R.id.cels).id -> requireActivity().findViewById<TextView>(R.id.hidden_temp).text = "metric"
                view.findViewById<RadioButton>(R.id.farg).id -> requireActivity().findViewById<TextView>(R.id.hidden_temp).text = "imperial"
            }

            when (view.findViewById<RadioGroup>(R.id.wind_unit_group).checkedRadioButtonId) {
                view.findViewById<RadioButton>(R.id.ms).id -> requireActivity().findViewById<TextView>(R.id.hidden_wind).text = "ms"
                view.findViewById<RadioButton>(R.id.kmh).id -> requireActivity().findViewById<TextView>(R.id.hidden_wind).text = "kmh"
                view.findViewById<RadioButton>(R.id.milh).id -> requireActivity().findViewById<TextView>(R.id.hidden_wind).text = "milh"
            }

            when (view.findViewById<RadioGroup>(R.id.pressure_unit_group).checkedRadioButtonId) {
                view.findViewById<RadioButton>(R.id.mmrtst).id -> requireActivity().findViewById<TextView>(R.id.hidden_pressure).text = "mmrtst"
                view.findViewById<RadioButton>(R.id.mbar).id -> requireActivity().findViewById<TextView>(R.id.hidden_pressure).text = "mbar"
            }
        }
    }
}