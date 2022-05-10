package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ForecastFragment(private val api: String) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ForecastAsync().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class ForecastAsync() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            view?.findViewById<ProgressBar>(R.id.forecast_loader)?.visibility = View.VISIBLE
            view?.findViewById<ConstraintLayout>(R.id.forecast_container)?.visibility = View.GONE
            requireActivity().findViewById<TextView>(R.id.error_text).visibility = View.GONE
            requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            val response:String? = try{
                URL("https://api.openweathermap.org/data/2.5/onecall?lat=58.04&lon=38.84&exclude=hourly,minutely,current&units=metric&lang=ru&appid=34dc93bfdf3425debd0c37b6580d8fe0").readText(Charsets.UTF_8)
            }catch (e: Exception){
                ""
            }
            return response
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            try {
                val jsonObject = JSONObject(result)

                val dates = arrayOf(R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6, R.id.date7)
                for (i in 1..7){
                    val date = SimpleDateFormat("dd.MM", Locale.ENGLISH).format(jsonObject.getJSONArray("daily").getJSONObject(i).getLong("dt") * 1000)
                    view?.findViewById<TextView>(dates[i-1])?.text = date
                }

                val dayTemps = arrayOf(R.id.temp_day1, R.id.temp_day2, R.id.temp_day3, R.id.temp_day4, R.id.temp_day5, R.id.temp_day6, R.id.temp_day7)
                for (i in 1..7){
                    val dayTemp = jsonObject.getJSONArray("daily").getJSONObject(i).getJSONObject("temp").getInt("day").toString() + "°"
                    view?.findViewById<TextView>(dayTemps[i-1])?.text = dayTemp
                }

                val nightTemps = arrayOf(R.id.temp_night1, R.id.temp_night2, R.id.temp_night3, R.id.temp_night4, R.id.temp_night5, R.id.temp_night6, R.id.temp_night7)
                for (i in 1..7){
                    val nightTemp = jsonObject.getJSONArray("daily").getJSONObject(i).getJSONObject("temp").getInt("night").toString() + "°"
                    view?.findViewById<TextView>(nightTemps[i-1])?.text = nightTemp
                }

                view?.findViewById<ProgressBar>(R.id.forecast_loader)?.visibility = View.GONE
                view?.findViewById<ConstraintLayout>(R.id.forecast_container)?.visibility = View.VISIBLE
                requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.VISIBLE
            } catch (e: Exception){
                view?.findViewById<ProgressBar>(R.id.forecast_loader)?.visibility = View.GONE
                requireActivity().findViewById<TextView>(R.id.error_text).visibility = View.VISIBLE
            }
        }
    }
}