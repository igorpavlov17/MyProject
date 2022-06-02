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
import androidx.fragment.app.viewModels
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
    private lateinit var myDbManager: MyDbManager
    private val weatherViewModel: WeatherViewModel by viewModels()

    private lateinit var address: TextView
    private lateinit var temp: TextView
    private lateinit var status: TextView
    private lateinit var sunriseText: TextView
    private lateinit var sunsetText: TextView
    private lateinit var windText: TextView
    private lateinit var pressureText: TextView
    private lateinit var humidityText: TextView
    private lateinit var lastUpdate: TextView
    private lateinit var loader: ProgressBar
    private lateinit var weatherContainer: ConstraintLayout
    private lateinit var errorText: TextView
    private lateinit var addressContainer: ConstraintLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()
        tempUnit  = myDbManager.getContentByTitle("temp_unit")
        city = requireActivity().findViewById<TextView>(R.id.address).text.toString()
        getWeather()
        view.findViewById<ImageView>(R.id.update).setOnClickListener {
            weatherViewModel.temp.value = null
            getWeather()
        }
    }

    private fun initViews(){
        address = requireActivity().findViewById(R.id.address)
        temp = view?.findViewById(R.id.temp)!!
        status = view?.findViewById(R.id.status)!!
        sunriseText = view?.findViewById(R.id.sunrise_text)!!
        sunsetText = view?.findViewById(R.id.sunset_text)!!
        windText = view?.findViewById(R.id.wind_text)!!
        pressureText = view?.findViewById(R.id.pressure_text)!!
        humidityText = view?.findViewById(R.id.humidity_text)!!
        lastUpdate = view?.findViewById(R.id.lastupdate)!!
        loader = view?.findViewById(R.id.loader)!!
        weatherContainer = view?.findViewById(R.id.weather_container)!!
        errorText = requireActivity().findViewById(R.id.error_text)!!
        addressContainer = requireActivity().findViewById(R.id.address_container)!!
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
        lifecycleScope.execute(onPreExecute = {
            loader.visibility = View.VISIBLE
            weatherContainer.visibility = View.GONE
            errorText.visibility = View.GONE
            addressContainer.visibility = View.GONE
        }, doInBackground = {
            try {
                if (weatherViewModel.temp.value != null) "-"
                else URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=$tempUnit&lang=ru&appid=$api").readText(Charsets.UTF_8)
            } catch (e: Exception){
                ""
            }
        }, onPostExecute = {
            if (it == "-"){
                address.text = weatherViewModel.address.value
                temp.text = weatherViewModel.temp.value
                status.text = weatherViewModel.status.value
                sunriseText.text = weatherViewModel.sunriseText.value
                sunsetText.text = weatherViewModel.sunsetText.value
                windText.text = weatherViewModel.windText.value
                pressureText.text = weatherViewModel.pressureText.value
                humidityText.text = weatherViewModel.humidityText.value
                lastUpdate.text = weatherViewModel.lastUpdate.value

                setVisible()
            }

            else try {
                val jsonObject = JSONObject(it)

                val address = jsonObject.getString("name") + ", " + jsonObject.getJSONObject("sys").getString("country")
                myDbManager.insertToDb("address", address, "")
                val temp = jsonObject.getJSONObject("main").getInt("temp").toString() + "°"
                myDbManager.insertToDb("temp", temp, "")
                val status = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description").capitalize()
                myDbManager.insertToDb("status", status, "")
                val pressure = jsonObject.getJSONObject("main").getString("pressure").toInt()
                myDbManager.insertToDb("pressure", pressure.toString(), "")
                val wind = jsonObject.getJSONObject("wind").getString("speed").toDouble()
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
                setVisible()
            } catch (e: Exception){
                if (myDbManager.getContentByTitle("address") == ""){
                    loader.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                }
                else {
                    loadFromDb()
                    setVisible()
                    if (it == "") Toast.makeText(activity, "Невозможно загрузить данные!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun loadFromDb(){
        address.text = myDbManager.getContentByTitle("address")
        lastUpdate.text = myDbManager.getContentByTitle("lastUpdate")
        status.text = myDbManager.getContentByTitle("status")
        temp.text = myDbManager.getContentByTitle("temp")
        sunriseText.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunrise").toLong()*1000))
        sunsetText.text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date(myDbManager.getContentByTitle("sunset").toLong()*1000))

        var wind = myDbManager.getContentByTitle("wind").toDouble()
        when (myDbManager.getContentByTitle("wind_unit")) {
            "ms" -> {
                if (tempUnit == "imperial"){
                    wind /= 2.237
                }
                windText.text = "%.1f".format(wind) + " м/с"
            }
            "kmh" -> {
                if (tempUnit == "imperial") wind /= 2.237
                wind *= 3.6
                windText.text = "%.1f".format(wind) + " км/ч"
            }
            "milh" -> {
                if (tempUnit == "metric") wind *= 2.237
                windText.text = "%.1f".format(wind) + " миль/ч"
            }
        }

        var pressure = myDbManager.getContentByTitle("pressure").toInt()
        if (myDbManager.getContentByTitle("pressure_unit") == "mmrtst"){
            pressure = (pressure/1.3332).toInt()
            pressureText.text = "$pressure мм рт.ст."
        } else pressureText.text = myDbManager.getContentByTitle("pressure") + " мбар"

        humidityText.text = myDbManager.getContentByTitle("humidity") + " %"

        weatherViewModel.address.value = address.text.toString()
        weatherViewModel.temp.value = temp.text.toString()
        weatherViewModel.status.value = status.text.toString()
        weatherViewModel.sunriseText.value = sunriseText.text.toString()
        weatherViewModel.sunsetText.value = sunsetText.text.toString()
        weatherViewModel.windText.value = windText.text.toString()
        weatherViewModel.pressureText.value = pressureText.text.toString()
        weatherViewModel.humidityText.value = humidityText.text.toString()
        weatherViewModel.lastUpdate.value = lastUpdate.text.toString()
    }

    private fun setVisible(){
        loader.visibility = View.GONE
        weatherContainer.visibility = View.VISIBLE
        addressContainer.visibility = View.VISIBLE
    }
}