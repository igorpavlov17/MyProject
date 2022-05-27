package com.example.myweather

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.example.myweather.db.MyDbManager
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : Fragment() {
    private lateinit var city: String
    private val api = "34dc93bfdf3425debd0c37b6580d8fe0"
    private lateinit var tempUnit: String
    lateinit var myDbManager: MyDbManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()
        tempUnit  = myDbManager.getContentByTitle("temp_unit")
        city = requireActivity().findViewById<TextView>(R.id.address).text.toString()
        getWeather()
        view.findViewById<ImageView>(R.id.update).setOnClickListener{
            getWeather()
        }
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

    @SuppressLint("SetTextI18n", "FragmentLiveDataObserve", "CutPasteId")
    fun getWeather(){
        lifecycleScope.execute(onPreExecute = {
            view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.VISIBLE
            view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.GONE
            requireActivity().findViewById<TextView>(R.id.error_text).visibility = View.GONE
            requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.GONE
        }, doInBackground = {
            try {
                URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$tempUnit&lang=ru&appid=$api").readText(Charsets.UTF_8)
            } catch (e: Exception){
                ""
            }
        }, onPostExecute = {
            try {
                val jsonObject = JSONObject(it)

                val address = jsonObject.getString("name") + ", " + jsonObject.getJSONObject("sys").getString("country")
                myDbManager.insertToDb("address", address, "")
                val temp = jsonObject.getJSONObject("main").getInt("temp").toString() + "°"
                myDbManager.insertToDb("temp", temp, "")
                val status = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description").capitalize()
                myDbManager.insertToDb("status", status, "")
                var pressure = jsonObject.getJSONObject("main").getString("pressure").toInt()
                myDbManager.insertToDb("pressure", pressure.toString(), "")
                var wind = jsonObject.getJSONObject("wind").getString("speed").toDouble()
                myDbManager.insertToDb("wind", wind.toString(), "")
                val humidity = jsonObject.getJSONObject("main").getString("humidity")
                myDbManager.insertToDb("humidity", humidity.toString(), "")
                val sunrise = jsonObject.getJSONObject("sys").getLong("sunrise")
                myDbManager.insertToDb("sunrise", sunrise.toString(), "")
                val sunset = jsonObject.getJSONObject("sys").getLong("sunset")
                myDbManager.insertToDb("sunset", sunset.toString(), "")
                val lastUpdate = "Последнее обновление: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(jsonObject.getLong("dt") * 1000)
                myDbManager.insertToDb("lastUpdate", lastUpdate, "")

                requireActivity().findViewById<TextView>(R.id.address)?.text = myDbManager.getContentByTitle("address")
                view?.findViewById<TextView>(R.id.lastupdate)?.text = myDbManager.getContentByTitle("lastUpdate")
                view?.findViewById<TextView>(R.id.status)?.text = myDbManager.getContentByTitle("status")
                view?.findViewById<TextView>(R.id.temp)?.text = myDbManager.getContentByTitle("temp")
                view?.findViewById<TextView>(R.id.sunrise_text)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunrise").toLong()*1000))
                view?.findViewById<TextView>(R.id.sunset_text)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunset").toLong()*1000))

                wind = myDbManager.getContentByTitle("wind").toDouble()
                when (myDbManager.getContentByTitle("wind_unit")) {
                    "ms" -> {
                        if (tempUnit == "imperial"){
                            wind /= 2.237
                        }
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

                if (myDbManager.getContentByTitle("pressure_unit") == "mmrtst"){
                    pressure = (myDbManager.getContentByTitle("pressure").toInt()/1.3332).toInt()
                    view?.findViewById<TextView>(R.id.pressure_text)?.text = "$pressure мм рт.ст."
                } else view?.findViewById<TextView>(R.id.pressure_text)?.text = myDbManager.getContentByTitle("pressure") + " мбар"

                view?.findViewById<TextView>(R.id.humidity_text)?.text = myDbManager.getContentByTitle("humidity") + " %"
                view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.GONE
                view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.VISIBLE
                requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.VISIBLE
            } catch (e: Exception){
                view?.findViewById<ProgressBar>(R.id.loader)?.visibility = View.GONE
                if (myDbManager.getContentByTitle("address") == ""){
                    requireActivity().findViewById<TextView>(R.id.error_text).visibility = View.VISIBLE
                }
                else {
                    requireActivity().findViewById<TextView>(R.id.address)?.text = myDbManager.getContentByTitle("address")
                    view?.findViewById<TextView>(R.id.lastupdate)?.text = myDbManager.getContentByTitle("lastUpdate")
                    view?.findViewById<TextView>(R.id.status)?.text = myDbManager.getContentByTitle("status")
                    view?.findViewById<TextView>(R.id.temp)?.text = myDbManager.getContentByTitle("temp")
                    view?.findViewById<TextView>(R.id.sunrise_text)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunrise").toLong()*1000))
                    view?.findViewById<TextView>(R.id.sunset_text)?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunset").toLong()*1000))

                    var wind = myDbManager.getContentByTitle("wind").toDouble()
                    when (myDbManager.getContentByTitle("wind_unit")) {
                        "ms" -> {
                            if (tempUnit == "imperial"){
                                wind /= 2.237
                            }
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

                    var pressure = myDbManager.getContentByTitle("pressure").toInt()
                    if (myDbManager.getContentByTitle("pressure_unit") == "mmrtst"){
                        pressure = (pressure/1.3332).toInt()
                        view?.findViewById<TextView>(R.id.pressure_text)?.text = "$pressure мм рт.ст."
                    } else view?.findViewById<TextView>(R.id.pressure_text)?.text = myDbManager.getContentByTitle("pressure") + " мбар"

                    view?.findViewById<TextView>(R.id.humidity_text)?.text = myDbManager.getContentByTitle("humidity") + " %"

                    view?.findViewById<ConstraintLayout>(R.id.weather_container)?.visibility = View.VISIBLE
                    requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.VISIBLE
                    Toast.makeText(activity, "Невозможно загрузить данные!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}