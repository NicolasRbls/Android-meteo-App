package com.example.android_meteo_app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android_meteo_app.domain.HourlyWeather
import com.example.android_meteo_app.domain.WeatherInfo
import com.example.android_meteo_app.ui.getWeatherIcon
import com.example.android_meteo_app.ui.viewmodels.DetailViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    navController: NavController,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.weatherInfo?.city?.name ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val weatherInfo = uiState.weatherInfo
                    if (weatherInfo != null && weatherInfo.city.id != 0) {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (uiState.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        val state = uiState
        when {
            state.isLoading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(paddingValues).padding(16.dp)
                )
            }
            state.weatherInfo != null -> {
                WeatherDetails(
                    weatherInfo = state.weatherInfo,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun WeatherDetails(weatherInfo: WeatherInfo, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = weatherInfo.city.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Icon(
            imageVector = getWeatherIcon(weatherInfo.currentCondition),
            contentDescription = weatherInfo.currentCondition.name,
            modifier = Modifier.size(128.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(text = "${weatherInfo.currentTemperature}°C", fontSize = 72.sp)
        Text(text = weatherInfo.currentCondition.name, style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            InfoItem("Min", "${weatherInfo.minTemperature}°C")
            InfoItem("Max", "${weatherInfo.maxTemperature}°C")
            InfoItem("Wind", "${weatherInfo.windSpeed} km/h")
        }

        Spacer(Modifier.height(32.dp))

        HourlyForecast(hourly = weatherInfo.hourlyForecast)
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun HourlyForecast(hourly: List<HourlyWeather>) {
    Column {
        Text(
            text = "Hourly Forecast",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(hourly) { weather ->
                HourlyItem(weather)
            }
        }
    }
}

@Composable
fun HourlyItem(weather: HourlyWeather) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    Card(modifier = Modifier.padding(vertical = 4.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = weather.time.format(formatter))
            Spacer(Modifier.height(8.dp))
            Icon(
                imageVector = getWeatherIcon(weather.condition),
                contentDescription = weather.condition.name,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = "${weather.temperature}°C", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(4.dp))
            Text(text = "${weather.windSpeed} km/h", fontSize = 12.sp)
        }
    }
}
