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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()
        tempUnit  = myDbManager.getContentByTitle("temp_unit")
        city = requireActivity().findViewById<TextView>(R.id.address).text.toString()
        getWeather()
        view.findViewById<ImageView>(R.id.update).setOnClickListener {
            getWeather()
        }
    }

    private fun <R> CoroutineScope.execute(
        onPreExecute: () -> Unit,
        doInBackground: () -> R,
        onPostExecute: (R) -> Unit
    ) = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO) {
            doInBackground()
        }
        onPostExecute(result)
    }

    private fun getWeather(){
        val loader = view?.findViewById<ProgressBar>(R.id.loader)
        val weatherContainer = view?.findViewById<ProgressBar>(R.id.weather_container)
        val errorText = requireActivity().findViewById<TextView>(R.id.error_text)
        val addressContainer = requireActivity().findViewById<ConstraintLayout>(R.id.address_container)

        lifecycleScope.execute(onPreExecute = {
            loader?.visibility = View.VISIBLE
            weatherContainer?.visibility = View.GONE
            errorText.visibility = View.GONE
            addressContainer.visibility = View.GONE
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

                loadFromDb()
                loader?.visibility = View.GONE
                weatherContainer?.visibility = View.VISIBLE
                addressContainer.visibility = View.VISIBLE
            } catch (e: Exception){
                if (myDbManager.getContentByTitle("address") == ""){
                    errorText.visibility = View.VISIBLE
                }
                else {
                    loadFromDb()
                    weatherContainer?.visibility = View.VISIBLE
                    addressContainer.visibility = View.VISIBLE
                    Toast.makeText(activity, "Невозможно загрузить данные!", Toast.LENGTH_SHORT).show()
                }
                loader?.visibility = View.GONE
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadFromDb(){
        val address = requireActivity().findViewById<TextView>(R.id.address)
        val lastUpdate = view?.findViewById<TextView>(R.id.lastupdate)
        val status = view?.findViewById<TextView>(R.id.status)
        val temp = view?.findViewById<TextView>(R.id.temp)
        val sunriseText = view?.findViewById<TextView>(R.id.sunrise_text)
        val sunsetText = view?.findViewById<TextView>(R.id.sunset_text)
        val windText = view?.findViewById<TextView>(R.id.wind_text)
        val pressureText = view?.findViewById<TextView>(R.id.pressure_text)
        val humidityText = view?.findViewById<TextView>(R.id.humidity_text)

        address.text = myDbManager.getContentByTitle("address")
        lastUpdate?.text = myDbManager.getContentByTitle("lastUpdate")
        status?.text = myDbManager.getContentByTitle("status")
        temp?.text = myDbManager.getContentByTitle("temp")
        sunriseText?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunrise").toLong()*1000))
        sunsetText?.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunset").toLong()*1000))

        var wind = myDbManager.getContentByTitle("wind").toDouble()
        when (myDbManager.getContentByTitle("wind_unit")) {
            "ms" -> {
                if (tempUnit == "imperial"){
                    wind /= 2.237
                }
                windText?.text = "%.1f".format(wind) + " м/с"
            }
            "kmh" -> {
                if (tempUnit == "imperial") wind /= 2.237
                wind *= 3.6
                windText?.text = "%.1f".format(wind) + " км/ч"
            }
            "milh" -> {
                if (tempUnit == "metric") wind *= 2.237
                windText?.text = "%.1f".format(wind) + " миль/ч"
            }
        }

        var pressure = myDbManager.getContentByTitle("pressure").toInt()
        if (myDbManager.getContentByTitle("pressure_unit") == "mmrtst"){
            pressure = (pressure/1.3332).toInt()
            pressureText?.text = "$pressure мм рт.ст."
        } else pressureText?.text = myDbManager.getContentByTitle("pressure") + " мбар"

        humidityText?.text = myDbManager.getContentByTitle("humidity") + " %"
    }
}