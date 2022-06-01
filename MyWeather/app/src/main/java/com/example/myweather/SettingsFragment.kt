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
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myweather.db.MyDbManager

class SettingsFragment : Fragment() {
    lateinit var myDbManager: MyDbManager
    var isTempOpened = false
    var isWindOpened = false
    var isPressureOpened = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null){
            isTempOpened = savedInstanceState.getBoolean("isTempOpened")
            isWindOpened = savedInstanceState.getBoolean("isWindOpened")
            isPressureOpened = savedInstanceState.getBoolean("isPressureOpened")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isTempOpened", isTempOpened)
        outState.putBoolean("isWindOpened", isWindOpened)
        outState.putBoolean("isPressureOpened", isPressureOpened)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tempUnitSetting = view.findViewById<ConstraintLayout>(R.id.temp_unit_setting)
        val windUnitSetting = view.findViewById<ConstraintLayout>(R.id.wind_unit_setting)
        val pressureUnitSetting = view.findViewById<ConstraintLayout>(R.id.pressure_unit_setting)

        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()

        if (isTempOpened) tempUnitSetting.visibility = View.VISIBLE
        if (isWindOpened) windUnitSetting.visibility = View.VISIBLE
        if (isPressureOpened) pressureUnitSetting.visibility = View.VISIBLE

        view.findViewById<ConstraintLayout>(R.id.temp_unit).setOnClickListener {
            if (tempUnitSetting.visibility == View.GONE){
                tempUnitSetting.visibility = View.VISIBLE
                isTempOpened = true
            }
            else{
                tempUnitSetting.visibility = View.GONE
                isTempOpened = false
            }
        }
        view.findViewById<ConstraintLayout>(R.id.wind_unit).setOnClickListener {
            if (windUnitSetting.visibility == View.GONE){
                windUnitSetting.visibility = View.VISIBLE
                isWindOpened = true
            }
            else {
                windUnitSetting.visibility = View.GONE
                isWindOpened = false
            }
        }
        view.findViewById<ConstraintLayout>(R.id.pressure_unit).setOnClickListener {
            if (pressureUnitSetting.visibility == View.GONE){
                pressureUnitSetting.visibility = View.VISIBLE
                isPressureOpened = true
            }
            else {
                pressureUnitSetting.visibility = View.GONE
                isPressureOpened = false
            }
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