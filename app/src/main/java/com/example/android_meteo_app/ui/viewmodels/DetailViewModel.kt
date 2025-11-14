package com.example.android_meteo_app.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.domain.WeatherInfo
import com.example.android_meteo_app.domain.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = true,
    val isFavorite: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    private val latitude: Float = savedStateHandle.get<String>("latitude")?.toFloatOrNull() ?: 0f
    private val longitude: Float = savedStateHandle.get<String>("longitude")?.toFloatOrNull() ?: 0f
    private val name: String = savedStateHandle.get<String>("name") ?: ""

    // This is a temporary city object until we get the full one from the repo
    private val city = City(id = 0, name = name, latitude = latitude.toDouble(), longitude = longitude.toDouble(), country = "", admin1 = "")


    init {
        fetchWeather()
        // We need a proper city ID to check if it's a favorite.
        // This will be a bit more complex. For now, we'll just fetch the weather.
    }

    private fun fetchWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            weatherRepository.getWeather(city)
                .onSuccess { weatherInfo ->
                    _uiState.update {
                        it.copy(weatherInfo = weatherInfo, isLoading = false)
                    }
                    // Now that we have a full city object, we can check if it's a favorite
                    observeIsFavorite(weatherInfo.city.id)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = "Failed to fetch weather: ${error.message}", isLoading = false)
                    }
                }
        }
    }

    private fun observeIsFavorite(cityId: Int) {
        viewModelScope.launch {
            weatherRepository.isFavorite(cityId).collectLatest { isFavorite ->
                _uiState.update { it.copy(isFavorite = isFavorite) }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val cityToToggle = _uiState.value.weatherInfo?.city ?: return@launch
            if (_uiState.value.isFavorite) {
                weatherRepository.removeFavoriteCity(cityToToggle)
            } else {
                weatherRepository.addFavoriteCity(cityToToggle)
            }
        }
    }
}
