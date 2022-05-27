package com.example.myweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class EditCityFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.change_city).setOnClickListener {
            if (view.findViewById<EditText>(R.id.edit_city_text).text.toString() != ""){
                requireActivity().findViewById<TextView>(R.id.address).text = view.findViewById<EditText>(R.id.edit_city_text).text.toString().capitalize()
                if (savedInstanceState == null) parentFragmentManager.beginTransaction().replace(R.id.main_container, WeatherFragment()).commit()
                //requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.VISIBLE
            } else Toast.makeText(activity, "Введите город!", Toast.LENGTH_SHORT).show()
        }
    }
}