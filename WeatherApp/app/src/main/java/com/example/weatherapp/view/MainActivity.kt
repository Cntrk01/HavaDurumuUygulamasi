package com.example.weatherapp.view



import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.weatherapp.R

import com.example.weatherapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel : MainViewModel
    private lateinit var GET:SharedPreferences
    private lateinit var SET:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GET=getSharedPreferences(packageName, MODE_PRIVATE)
        SET=GET.edit()
        viewModel=ViewModelProvider(this).get(MainViewModel::class.java)
        var cName=GET.getString("cityName",edt_city_name.text.toString())
        edt_city_name.setText(cName)
        viewModel.refreshData(cName!!)

        getLiveData()

        swipe_refresh_layout.setOnClickListener {

            ll_data_view.visibility=View.GONE
            tv_error.visibility=View.GONE
            pb_loading.visibility=View.GONE

            var cityName=GET.getString("cityName",cName)
            edt_city_name.setText(cityName)
            viewModel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing=false
        }

        img_search_city.setOnClickListener {
            val cityName=edt_city_name.text.toString()
            SET.putString("cityName",cityName)
            SET.apply()
            viewModel.refreshData(cityName)
            getLiveData()

        }

    }

    private fun getLiveData() {

         viewModel.weather_data.observe(this, Observer {
             it?.let{
                ll_data_view.visibility= View.VISIBLE
                 pb_loading.visibility=View.GONE
                 tv_degree.text=it.main.temp.toString()
                 tv_city_code.text=it.sys.country
                 tv_city_name.text=it.name
                 tv_humidity.text=it.main.humidity.toString()
                 tv_wind_speed.text=it.wind.speed.toString()
                 tv_lat.text=it.coord.lat.toString()
                 tv_lon.text=it.coord.lon.toString()

                 Glide.with(this).load("http://api.openweathermap.org/img/wn/"+it.weather.get(0).icon+"@2x.png")
                     .into(img_weather_pictures)
             }
         })
        viewModel.weather_error.observe(this, Observer {
            it?.let {
                if(it){
                    tv_error.visibility=View.VISIBLE
                    ll_data_view.visibility= View.GONE
                    pb_loading.visibility=View.GONE
                }else{
                    tv_error.visibility=View.GONE
                }
            }
        })
    }
}