package com.example.myweather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide

class MapFragment : Fragment() {
    private val api = "34dc93bfdf3425debd0c37b6580d8fe0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val s: String = arguments?.getString(mapType).toString()
        Glide.with(view).load("https://tile.openweathermap.org/map/$s/0/0/0.png?appid=$api").into(view.findViewById(R.id.map_image))
    }

    companion object {
        private var mapType: String = "тип карты"

        @JvmStatic
        fun newInstance(s: String): Fragment = MapFragment().apply {
            val bundle = Bundle()
            bundle.putString(mapType, s)
            arguments = bundle
        }
    }
}