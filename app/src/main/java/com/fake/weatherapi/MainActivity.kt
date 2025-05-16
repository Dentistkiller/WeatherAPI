package com.fake.weatherapi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val apiKey = "YOUR_API_KEY"
    private lateinit var weatherService: WeatherService
    private lateinit var searchQuery: EditText
    private lateinit var searchBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        searchQuery=findViewById(R.id.etSearchQuery)
        searchBtn=findViewById(R.id.searchBtn)


        searchBtn.setOnClickListener {
            val cityName = searchQuery.text.toString().uppercase();
            weatherService = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherService::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                val weatherData = weatherService.getWeather("$cityName", apiKey)
                withContext(Dispatchers.Main) {
                    updateUI(weatherData)
                }
            }
        }


    }
    private fun updateUI(weatherData: WeatherData) {
        findViewById<TextView>(R.id.etSearchQuery).text = weatherData.name
        findViewById<TextView>(R.id.textViewTemperature).text =
            "${weatherData.main.temp.toInt()}°K"
        val iconUrl = "https://openweathermap.org/img/w/${weatherData.weather[0].icon}.png"
        Glide.with(this)
            .load(iconUrl)
            .into(findViewById(R.id.imageViewWeatherIcon))
    }
}
data class WeatherData(
    val name: String,
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double
)

data class Weather(
    val icon: String
)
