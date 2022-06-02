package com.example.myweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    val address = MutableLiveData<String>()
    val temp = MutableLiveData<String>()
    val status = MutableLiveData<String>()
    val sunriseText = MutableLiveData<String>()
    val sunsetText = MutableLiveData<String>()
    val windText = MutableLiveData<String>()
    val pressureText = MutableLiveData<String>()
    val humidityText = MutableLiveData<String>()
    val lastUpdate = MutableLiveData<String>()
}