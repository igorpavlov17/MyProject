package com.example.myweather

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ForecastFragment : Fragment() {
    private val api = "34dc93bfdf3425debd0c37b6580d8fe0"
    private lateinit var myDbManager: MyDbManager
    private val forecastViewModel: ForecastViewModel by viewModels()

    private lateinit var forecastLoader: ProgressBar
    private lateinit var forecastContainer: ConstraintLayout
    private lateinit var lastForecastUpdateText: TextView
    private lateinit var updateForecast: ImageView
    private lateinit var errorText: TextView
    private lateinit var addressContainer: ConstraintLayout

    private val dates = arrayOf(R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6, R.id.date7)
    private val dayTemps = arrayOf(R.id.temp_day1, R.id.temp_day2, R.id.temp_day3, R.id.temp_day4, R.id.temp_day5, R.id.temp_day6, R.id.temp_day7)
    private val nightTemps = arrayOf(R.id.temp_night1, R.id.temp_night2, R.id.temp_night3, R.id.temp_night4, R.id.temp_night5, R.id.temp_night6, R.id.temp_night7)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()
        getForecast()
        view.findViewById<ImageView>(R.id.update_forecast).setOnClickListener{
            forecastViewModel.dates.value = null
            getForecast()
        }
    }

    private fun initViews(){
        forecastLoader = view?.findViewById(R.id.forecast_loader)!!
        forecastContainer = view?.findViewById(R.id.forecast_container)!!
        lastForecastUpdateText = view?.findViewById(R.id.last_forecast_update)!!
        updateForecast = view?.findViewById(R.id.update_forecast)!!
        errorText = requireActivity().findViewById(R.id.error_text)!!
        addressContainer = requireActivity().findViewById(R.id.address_container)!!
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

    private fun getForecast(){
        lifecycleScope.execute(onPreExecute = {
            forecastLoader.visibility = View.VISIBLE
            forecastContainer.visibility = View.GONE
            lastForecastUpdateText.visibility = View.GONE
            updateForecast.visibility = View.GONE
            errorText.visibility = View.GONE
            addressContainer.visibility = View.GONE
        }, doInBackground = {
            try {
                if (forecastViewModel.dates.value != null) "-"
                else URL("https://api.openweathermap.org/data/2.5/onecall?lat=58.04&lon=38.84&exclude=hourly,minutely,alerts&units=metric&lang=ru&appid=$api").readText(Charsets.UTF_8)
            } catch (e: Exception){
                ""
            }
        }, onPostExecute = {
            if (it == "-") {
                for (i in 1..7){
                    view?.findViewById<TextView>(dates[i-1])?.text = forecastViewModel.dates.value!!.split(" ")[i]
                    view?.findViewById<TextView>(dayTemps[i-1])?.text = forecastViewModel.dayTemps.value!!.split(" ")[i]
                    view?.findViewById<TextView>(nightTemps[i-1])?.text = forecastViewModel.nightTemps.value!!.split(" ")[i]
                }
                lastForecastUpdateText.text = forecastViewModel.lastForecastUpdate.value
                setVisible()
            }
            else try {
                val jsonObject = JSONObject(it)
                for (i in 1..7){
                    val date = SimpleDateFormat("dd.MM", Locale.ENGLISH).format(jsonObject.getJSONArray("daily").getJSONObject(i).getLong("dt") * 1000)
                    val dayTemp = jsonObject.getJSONArray("daily").getJSONObject(i).getJSONObject("temp").getInt("day").toString() + "°"
                    val nightTemp = jsonObject.getJSONArray("daily").getJSONObject(i).getJSONObject("temp").getInt("night").toString() + "°"
                    myDbManager.insertToDb(date, dayTemp, nightTemp)
                    myDbManager.insertToDb("lastLoadedDate$i", date, "")
                }

                val lastForecastUpdate = "Последнее обновление: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(jsonObject.getJSONObject("current").getLong("dt") * 1000)
                myDbManager.insertToDb("lastForecastUpdate", lastForecastUpdate, "")

                loadFromDb()
            } catch (e: Exception){
                if (myDbManager.getContentByTitle("lastForecastUpdate") == ""){
                    forecastLoader.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                }
                else{
                    loadFromDb()
                    if (it == "") Toast.makeText(activity, "Невозможно загрузить данные!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadFromDb(){
        for (i in 1..7){
            view?.findViewById<TextView>(dates[i-1])?.text = myDbManager.getContentByTitle("lastLoadedDate$i")
            view?.findViewById<TextView>(dayTemps[i-1])?.text = myDbManager.getContentByTitle(myDbManager.getContentByTitle("lastLoadedDate$i"))
            view?.findViewById<TextView>(nightTemps[i-1])?.text = myDbManager.getContent2ByTitle(myDbManager.getContentByTitle("lastLoadedDate$i"))

            forecastViewModel.dates.value += " " + view?.findViewById<TextView>(dates[i-1])?.text.toString()
            forecastViewModel.dayTemps.value += " " + view?.findViewById<TextView>(dayTemps[i-1])?.text.toString()
            forecastViewModel.nightTemps.value += " " + view?.findViewById<TextView>(nightTemps[i-1])?.text.toString()
        }
        lastForecastUpdateText.text = myDbManager.getContentByTitle("lastForecastUpdate")
        forecastViewModel.lastForecastUpdate.value = lastForecastUpdateText.text.toString()
        setVisible()
    }

    private fun setVisible(){
        forecastLoader.visibility = View.GONE
        lastForecastUpdateText.visibility = View.VISIBLE
        updateForecast.visibility = View.VISIBLE
        forecastContainer.visibility = View.VISIBLE
        addressContainer.visibility = View.VISIBLE
    }
}