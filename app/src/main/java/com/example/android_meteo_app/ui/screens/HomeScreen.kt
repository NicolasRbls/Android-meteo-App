package com.example.android_meteo_app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.ui.navigation.Screen
import com.example.android_meteo_app.ui.viewmodels.HomeViewModel
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.currentUserLocation) {
        uiState.currentUserLocation?.let { location ->
            navController.navigate(
                Screen.Details.createRoute(
                    cityId = 0, // 0 indicates it's a location, not a saved city
                    latitude = location.latitude.toFloat(),
                    longitude = location.longitude.toFloat(),
                    name = "Current Location"
                )
            )
            viewModel.onLocationNavigationConsumed()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true || permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            viewModel.onLocationRequested()
        } else {
            // Handle permission denial by showing a snackbar
            viewModel.onPermissionDenied()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meteo App") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                isSearching = uiState.isSearching,
                onLocationClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )

            if (uiState.searchQuery.isNotBlank()) {
                LazyColumn {
                    items(uiState.searchResults) { city ->
                        CityRow(city = city) {
                            navController.navigate(
                                Screen.Details.createRoute(
                                    cityId = city.id,
                                    latitude = city.latitude.toFloat(),
                                    longitude = city.longitude.toFloat(),
                                    name = city.name
                                )
                            )
                        }
                    }
                }
            } else {
                FavoriteCitiesList(cities = uiState.favoriteCities) { clickedCity ->
                     navController.navigate(
                        Screen.Details.createRoute(
                            cityId = clickedCity.id,
                            latitude = clickedCity.latitude.toFloat(),
                            longitude = clickedCity.longitude.toFloat(),
                            name = clickedCity.name
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean,
    onLocationClick: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search for a city...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        leadingIcon = {
            IconButton(onClick = onLocationClick) {
                Icon(Icons.Default.LocationOn, contentDescription = "Use current location")
            }
        },
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator()
            }
        }
    )
}

@Composable
fun FavoriteCitiesList(cities: List<com.example.android_meteo_app.domain.FavoriteWeatherSummary>, onCityClick: (City) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Favorites",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (cities.isEmpty()) {
            item {
                Text("You have no favorite cities yet.")
            }
        } else {
            items(cities) { summary ->
                FavoriteCityRow(summary = summary, onCityClick = onCityClick)
            }
        }
    }
}

@Composable
fun FavoriteCityRow(summary: com.example.android_meteo_app.domain.FavoriteWeatherSummary, onCityClick: (City) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCityClick(summary.city) }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val displayText = buildString {
                    append(summary.city.name)
                    if (summary.city.country.isNotBlank()) {
                        append(", ${summary.city.country}")
                    }
                }
                Text(text = displayText)
            }
            summary.weatherInfo?.let { weather ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${weather.currentTemperature}°C",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    // Here you could add an icon for the weather.currentCondition
                }
            } ?: CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun CityRow(city: City, onCityClick: (City) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCityClick(city) }
    ) {
        val displayText = buildString {
            append(city.name)
            if (city.admin1?.isNotBlank() == true) {
                append(", ${city.admin1}")
            }
            if (city.country.isNotBlank()) {
                append(", ${city.country}")
            }
        }
        Text(
            text = displayText,
            modifier = Modifier.padding(16.dp)
        )
    }
}
