package com.desla.weatherapp

import android.annotation.SuppressLint

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.widget.TextView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherRvAdapter(
    private val context: MainActivity,
    private val weatherRvModelArrayList: ArrayList<WeatherRvModel>
) : RecyclerView.Adapter<WeatherRvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.weather_rv_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (time, temperature, icon, windSpeed) = weatherRvModelArrayList[position]
        holder.temperatureTV.text = "$temperature°C"
        Picasso.get().load("http:$icon").into(holder.conditionIV)
        holder.windTV.text = windSpeed + "Km/h"
        val input = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.ENGLISH)
        val output = SimpleDateFormat("hh:mm aa", Locale.ENGLISH)
        try {
            val t: Date = input.parse(time)
            holder.timeTV.text = output.format(t)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return weatherRvModelArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val windTV: TextView = itemView.findViewById(R.id.idTVWindSpeed)
        val temperatureTV: TextView = itemView.findViewById(R.id.idTVTemperature)
        val timeTV: TextView = itemView.findViewById(R.id.idTVTime)
        val conditionIV: ImageView = itemView.findViewById(R.id.idTVCondition)

    }
}