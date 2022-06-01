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
    lateinit var myDbManager: MyDbManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_forecast, container, false)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myDbManager = MyDbManager(requireContext())
        myDbManager.openDB()
        getForecast()
        view.findViewById<ImageView>(R.id.update_forecast).setOnClickListener{
            getForecast()
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


    private fun getForecast(){
        val forecastLoader = view?.findViewById<ProgressBar>(R.id.forecast_loader)
        val forecastContainer = view?.findViewById<ProgressBar>(R.id.forecast_container)
        val lastForecastUpdateText = view?.findViewById<TextView>(R.id.last_forecast_update)
        val updateForecast = view?.findViewById<ImageView>(R.id.update_forecast)
        val errorText = requireActivity().findViewById<TextView>(R.id.error_text)
        val addressContainer = requireActivity().findViewById<ConstraintLayout>(R.id.address_container)

        lifecycleScope.execute(onPreExecute = {


            forecastLoader?.visibility = View.VISIBLE
            forecastContainer?.visibility = View.GONE
            lastForecastUpdateText?.visibility = View.GONE
            updateForecast?.visibility = View.GONE
            errorText.visibility = View.GONE
            addressContainer.visibility = View.GONE
        }, doInBackground = {
            try {
                URL("https://api.openweathermap.org/data/2.5/onecall?lat=58.04&lon=38.84&exclude=hourly,minutely,alerts&units=metric&lang=ru&appid=$api").readText(Charsets.UTF_8)
            } catch (e: Exception){
                ""
            }
        }, onPostExecute = {
            try {
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
                forecastLoader?.visibility = View.GONE
            } catch (e: Exception){
                forecastLoader?.visibility = View.GONE
                if (myDbManager.getContentByTitle("lastForecastUpdate") == "") errorText.visibility = View.VISIBLE
                else{
                    loadFromDb()
                    Toast.makeText(activity, "Невозможно загрузить данные!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadFromDb(){
        val lastForecastUpdateText = view?.findViewById<TextView>(R.id.last_forecast_update)

        val dates = arrayOf(R.id.date1, R.id.date2, R.id.date3, R.id.date4, R.id.date5, R.id.date6, R.id.date7)
        val dayTemps = arrayOf(R.id.temp_day1, R.id.temp_day2, R.id.temp_day3, R.id.temp_day4, R.id.temp_day5, R.id.temp_day6, R.id.temp_day7)
        val nightTemps = arrayOf(R.id.temp_night1, R.id.temp_night2, R.id.temp_night3, R.id.temp_night4, R.id.temp_night5, R.id.temp_night6, R.id.temp_night7)

        for (i in 1..7){
            view?.findViewById<TextView>(dates[i-1])?.text = myDbManager.getContentByTitle("lastLoadedDate$i")
            view?.findViewById<TextView>(dayTemps[i-1])?.text = myDbManager.getContentByTitle(myDbManager.getContentByTitle("lastLoadedDate$i"))
            view?.findViewById<TextView>(nightTemps[i-1])?.text = myDbManager.getContent2ByTitle(myDbManager.getContentByTitle("lastLoadedDate$i"))
        }
        lastForecastUpdateText?.text = myDbManager.getContentByTitle("lastForecastUpdate")

        lastForecastUpdateText?.visibility = View.VISIBLE
        view?.findViewById<ImageView>(R.id.update_forecast)?.visibility = View.VISIBLE
        view?.findViewById<ConstraintLayout>(R.id.forecast_container)?.visibility = View.VISIBLE
        requireActivity().findViewById<ConstraintLayout>(R.id.address_container).visibility = View.VISIBLE
    }
}