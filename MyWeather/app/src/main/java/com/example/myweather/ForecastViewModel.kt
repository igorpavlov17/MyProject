package com.example.myweather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForecastViewModel : ViewModel() {
    var dates = MutableLiveData<String>()
    var dayTemps = MutableLiveData<String>()
    var nightTemps = MutableLiveData<String>()
    var lastForecastUpdate = MutableLiveData<String>()
}