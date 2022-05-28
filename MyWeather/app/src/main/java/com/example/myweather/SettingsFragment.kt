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
import com.example.myweather.db.MyDbManager

class SettingsFragment : Fragment() {
    lateinit var myDbManager: MyDbManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()

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
                view.findViewById<RadioButton>(R.id.cels).id -> myDbManager.insertToDb("temp_unit", "metric", "")
                view.findViewById<RadioButton>(R.id.farg).id -> myDbManager.insertToDb("temp_unit", "imperial", "")
            }

            when (view.findViewById<RadioGroup>(R.id.wind_unit_group).checkedRadioButtonId) {
                view.findViewById<RadioButton>(R.id.ms).id -> myDbManager.insertToDb("wind_unit", "ms", "")
                view.findViewById<RadioButton>(R.id.kmh).id -> myDbManager.insertToDb("wind_unit", "kmh", "")
                view.findViewById<RadioButton>(R.id.milh).id -> myDbManager.insertToDb("wind_unit", "milh", "")
            }

            when (view.findViewById<RadioGroup>(R.id.pressure_unit_group).checkedRadioButtonId) {
                view.findViewById<RadioButton>(R.id.mmrtst).id -> myDbManager.insertToDb("pressure_unit", "mmrtst", "")
                view.findViewById<RadioButton>(R.id.mbar).id -> myDbManager.insertToDb("pressure_unit", "mbar", "")
            }
        }
    }
}