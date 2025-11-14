package com.example.android_meteo_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.domain.FavoriteWeatherSummary
import com.example.android_meteo_app.domain.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val searchQuery: String = "",
    val searchResults: List<City> = emptyList(),
    val favoriteCities: List<FavoriteWeatherSummary> = emptyList(),
    val isSearching: Boolean = false,
    val currentUserLocation: android.location.Location? = null,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationTracker: com.example.android_meteo_app.domain.LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            weatherRepository.getFavoriteCities().collectLatest { favorites ->
                val summaries = favorites.map { city ->
                    val weatherResult = weatherRepository.getWeather(city)
                    FavoriteWeatherSummary(city, weatherResult.getOrNull())
                }
                _uiState.update { it.copy(favoriteCities = summaries) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.length < 2) {
                _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
                return@launch
            }
            _uiState.update { it.copy(isSearching = true) }
            delay(500) // Debounce
            weatherRepository.searchCity(query)
                .onSuccess { results ->
                    _uiState.update {
                        it.copy(searchResults = results, isSearching = false)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(error = "Search failed: ${error.message}", isSearching = false)
                    }
                }
        }
    }

    fun onLocationRequested() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            locationTracker.getCurrentLocation()?.let { location ->
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        currentUserLocation = location
                    )
                }
            } ?: _uiState.update {
                it.copy(
                    isSearching = false,
                    error = "Could not retrieve location. Make sure GPS is enabled and permissions are granted."
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onLocationNavigationConsumed() {
        _uiState.update { it.copy(currentUserLocation = null) }
    }

    fun onPermissionDenied() {
        _uiState.update { it.copy(error = "Location permissions denied. Please grant permissions to use this feature.") }
    }
}
