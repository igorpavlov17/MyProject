
package com.example.myweather

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val city = "Рыбинск"
    val api = "34dc93bfdf3425debd0c37b6580d8fe0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WeatherAsync().execute()
    }

    inner class WeatherAsync() : AsyncTask<String, Void, String>() {
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ConstraintLayout>(R.id.container).visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String {
            return URL("https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&lang=ru&appid=$api").readText(Charsets.UTF_8)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val jsonObject = JSONObject(result)
            val weather = jsonObject.getJSONArray("weather").getJSONObject(0)
            val lastUpdate = "Последнее обновление: " + SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH).format(Date(jsonObject.getLong("dt")*1000))
            val temp = jsonObject.getJSONObject("main").getInt("temp").toString() + "°"
            val status = weather.getString("description")
            val address = jsonObject.getString("name") + ", " + jsonObject.getJSONObject("sys").getString("country")

            findViewById<TextView>(R.id.time).text = SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date())
            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.lastupdate).text =  lastUpdate
            findViewById<TextView>(R.id.status).text = status.capitalize()
            findViewById<TextView>(R.id.temp).text = temp
            findViewById<ConstraintLayout>(R.id.container).visibility = View.VISIBLE
        }
    }
}