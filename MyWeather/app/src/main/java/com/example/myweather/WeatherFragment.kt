package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment(private val city: String, private val api: String) : Fragment() {
    private lateinit var tempUnit: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tempUnit  = requireActivity().findViewById<TextView>(R.id.hidden_temp).text.toString()
        getWeather()
    }

    private fun <R> CoroutineScope.execute(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO){
            doInBackground()
        }
        onPostExecute(result)
    }

    @SuppressLint("SetTextI18n")
    fun getWeather(){
        lifecycleScope.execute(onPreExecute = {
            view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.VISIBLE
            view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.GONE
            requireActivity().findViewById<TextView>(R.id.error_text).visibility = View.GONE
            requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.GONE
        }, doInBackground = {
            URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$tempUnit&lang=ru&appid=$api").readText(Charsets.UTF_8)
        }, onPostExecute = {
            try {
                val jsonObject = JSONObject(it)
                var pressure = jsonObject.getJSONObject("main").getString("pressure").toInt()
                var wind = jsonObject.getJSONObject("wind").getString("speed").toDouble()
                val humidity = jsonObject.getJSONObject("main").getString("humidity")
                val sunrise = jsonObject.getJSONObject("sys").getLong("sunrise")
                val sunset = jsonObject.getJSONObject("sys").getLong("sunset")
                val lastUpdate = "Последнее обновление: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(Date(jsonObject.getLong("dt") * 1000))
                val temp = jsonObject.getJSONObject("main").getInt("temp").toString() + "°"
                val status = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
                val address = jsonObject.getString("name") + ", " + jsonObject.getJSONObject("sys").getString("country")

                requireActivity().findViewById<TextView>(R.id.time)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())
                requireActivity().findViewById<TextView>(R.id.address)?.text = address

                view?.findViewById<TextView>(R.id.lastupdate)?.text = lastUpdate
                view?.findViewById<TextView>(R.id.status)?.text = status.capitalize()
                view?.findViewById<TextView>(R.id.temp)?.text = temp
                view?.findViewById<TextView>(R.id.sunrise_text)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(sunrise*1000))
                view?.findViewById<TextView>(R.id.sunset_text)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(sunset*1000))

                when (requireActivity().findViewById<TextView>(R.id.hidden_wind).text) {
                    "ms" -> {
                        if (tempUnit == "imperial") wind /= 2.237
                        view?.findViewById<TextView>(R.id.wind_text)?.text = "%.1f".format(wind) + " м/с"
                    }
                    "kmh" -> {
                        if (tempUnit == "imperial") wind /= 2.237
                        wind *= 3.6
                        view?.findViewById<TextView>(R.id.wind_text)?.text = "%.1f".format(wind) + " км/ч"
                    }
                    "milh" -> {
                        if (tempUnit == "metric") wind *= 2.237
                        view?.findViewById<TextView>(R.id.wind_text)?.text = "%.1f".format(wind) + " миль/ч"
                    }
                }

                if (requireActivity().findViewById<TextView>(R.id.hidden_pressure).text == "mmrtst"){
                    pressure = (pressure/1.3332).toInt()
                    view?.findViewById<TextView>(R.id.pressure_text)?.text = "$pressure мм рт.ст."
                } else view?.findViewById<TextView>(R.id.pressure_text)?.text = "$pressure мбар"

                view?.findViewById<TextView>(R.id.humidity_text)?.text = "$humidity %"
                view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.GONE
                view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.VISIBLE
                requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.VISIBLE
            } catch (e: Exception){
                view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.GONE
                requireActivity().findViewById<TextView>(R.id.error_text).visibility = View.VISIBLE
            }
        })
    }
}