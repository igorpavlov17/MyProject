package com.example.myweather

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : Fragment() {
    var city = "Рыбинск"
    private val api = "34dc93bfdf3425debd0c37b6580d8fe0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        WeatherAsync().execute()

        view.findViewById<ImageView>(R.id.edit_city).setOnClickListener {
            parentFragmentManager.beginTransaction().add(R.id.main_container, EditCityFragment()).addToBackStack(null).commit()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class WeatherAsync() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.VISIBLE
            view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String {
            return URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&lang=ru&appid=$api").readText(Charsets.UTF_8)
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val jsonObject = JSONObject(result)
            var pressure = jsonObject.getJSONObject("main").getString("pressure").toInt()
            pressure = (pressure/1.3332).toInt()
            val humidity = jsonObject.getJSONObject("main").getString("humidity")
            val sunrise:Long = jsonObject.getJSONObject("sys").getLong("sunrise")
            val sunset:Long = jsonObject.getJSONObject("sys").getLong("sunset")
            val windSpeed = jsonObject.getJSONObject("wind").getString("speed")
            val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
            val lastUpdate = "Последнее обновление: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(Date(jsonObject.getLong("dt") * 1000))
            val temp = jsonObject.getJSONObject("main").getInt("temp").toString() + "°"
            val status = weather.getString("description")
            val address = jsonObject.getString("name") + ", " + jsonObject.getJSONObject("sys").getString("country")

            view?.findViewById<TextView>(R.id.time)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(
                Date()
            )
            view?.findViewById<TextView>(R.id.address)?.text = address
            view?.findViewById<TextView>(R.id.lastupdate)?.text = lastUpdate
            view?.findViewById<TextView>(R.id.status)?.text = status.capitalize()
            view?.findViewById<TextView>(R.id.temp)?.text = temp

            view?.findViewById<TextView>(R.id.sunrise)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(sunrise*1000))
            view?.findViewById<TextView>(R.id.sunset)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(sunset*1000))
            view?.findViewById<TextView>(R.id.wind)?.text = "$windSpeed м/c"
            view?.findViewById<TextView>(R.id.pressure)?.text = "$pressure мм рт.ст."
            view?.findViewById<TextView>(R.id.humidity)?.text = "$humidity %"

            view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.GONE
            view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.VISIBLE
        }
    }
}