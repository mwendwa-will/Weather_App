package com.desla.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.desla.weatherapp.databinding.ActivityMainBinding
import com.desla.weatherapp.databinding.WeatherRvListBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var binding2: WeatherRvListBinding
    private lateinit var weatherRvModelArrayList: ArrayList<WeatherRvModel>
    private lateinit var weatherRvAdapter: WeatherRvAdapter
    private lateinit var locationManager: LocationManager
    private var PERMISSION_CODE = 1
    private lateinit var cityName: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding2 = WeatherRvListBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_main)
        val view = binding.root
        binding.idRLHome
        binding.idPBLoading
        binding.idTVCityName
        binding.idTVTemperature
        binding.idTVCondition
        binding.idRVWeather
        binding.idEdtCity
        binding.idIVBack
        binding.IDIVIcon
        binding.idIVSearch

        weatherRvModelArrayList = ArrayList()
        weatherRvAdapter = WeatherRvAdapter(this, weatherRvModelArrayList)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_CODE
            )
        }
        val location: Location =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) as Location
        cityName = getCityName(location.longitude, location.latitude)
        weatherInfo(cityName)

        binding.idIVSearch.setOnClickListener(
            View.OnClickListener() {

                fun onClick(v: View) {
                    val city: String = binding.idEdtCity.text.toString()
                    if (city.isEmpty()) {
                        Toast.makeText(this, "Please enter city name", Toast.LENGTH_SHORT).show()
                    } else {
                        binding.idTVCityName.text = city
                        weatherInfo(city)
                    }
                }
            },
        )


    }

    //This method will be used to handle the permissions
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please grant permissions", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }


    //This method will be used to get the city name using longitude and latitude
    private fun getCityName(longitude: Double, latitude: Double): String {
        var cityName = "Not Found"
        val gcd = Geocoder(baseContext, Locale.getDefault())
        try {
            val addresses = gcd.getFromLocation(latitude, longitude, 10)
            for (adr in addresses) {
                if (adr != null) {
                    val city = adr.locality
                    if (city != null && city != "") {
                        cityName = city
                    } else {
                        Log.d("TAG", "CITY NOT FOUND ")
                        Toast.makeText(this, "User City not Found..", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return cityName
    }


    //This method will be used to get the weather information
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun weatherInfo(cityName: String) {
        val url: String =
            "http://api.weatherapi.com/v1/forecast.json?key=f2bfd6b83504417fbee75251212109&q=" + cityName + "i&days=1&aqi=yes&alerts=yes"
        binding.idTVCityName.text = cityName
        val requestQueue = Volley.newRequestQueue(this)


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, {
            binding.idPBLoading.visibility = View.GONE
            binding.idRLHome.visibility = View.VISIBLE
            weatherRvModelArrayList.clear()

            try {
                val temperature = JSONObject("current").getString("temp_c")
                binding.idTVTemperature.text = "$temperature°C"
                val isDay = JSONObject("current").getInt("is_day")
                val condition = JSONObject("current").getJSONObject("condition").getString("text")
                val conditionIcon =
                    JSONObject("current").getJSONObject("condition").getString("icon")
                Picasso.get().load("http:$conditionIcon").into(binding.IDIVIcon)
                binding.idTVCondition.text = condition
                if (isDay == 1) {
                    //Morning Time
                    Picasso.get()
                        .load("https://www.worldwanderista.com/wp-content/uploads/2015/02/sunrise-Cuba.jpg")
                        .into(binding.idIVBack)

                } else {
                    //Night Time
                    Picasso.get()
                        .load("http://www.harvestingrainwater.com/wp-content/gallery/Night-Sky-Harvesting-A/1-Milky_Way_Night_Sky_Black_Rock_Desert_Nevada.jpg")
                        .into(binding.idIVBack)

                }
                val forecast = JSONObject("forecast")
                val forecastArray = forecast.getJSONArray("forecastday").getJSONObject(0)
                val hourArray = forecastArray.getJSONArray("hour")
                val i: Int = 0
                while (i < hourArray.length()) {
                    val hourObj = hourArray.getJSONObject(i)
                    val time = hourObj.getString("time")
                    val temp = hourObj.getString("temp_c")
                    val img = hourObj.getJSONObject("condition").getString("icon")
                    val wind = hourObj.getString("wind_kph")
                    weatherRvModelArrayList.add(WeatherRvModel(time, temp, img, wind))
                }


                weatherRvAdapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                e.printStackTrace()
            }


        }, {
            Toast.makeText(this, "Please enter valid city name..", Toast.LENGTH_SHORT).show()
        })
        requestQueue.add(jsonObjectRequest)

    }

}

