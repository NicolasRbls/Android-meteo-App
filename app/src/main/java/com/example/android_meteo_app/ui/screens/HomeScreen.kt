package com.example.android_meteo_app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.ui.navigation.Screen
import com.example.android_meteo_app.ui.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Meteo App") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                isSearching = uiState.isSearching
            )

            if (uiState.searchQuery.isNotBlank()) {
                LazyColumn {
                    items(uiState.searchResults) { city ->
                        CityRow(city = city) {
                            // Navigate to details
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
                // Show favorites
                FavoriteCitiesList(cities = uiState.favoriteCities) { city ->
                    // Navigate to details
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
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearching: Boolean
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search for a city...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        trailingIcon = {
            if (isSearching) {
                CircularProgressIndicator()
            }
        }
    )
}

@Composable
fun FavoriteCitiesList(cities: List<City>, onCityClick: (City) -> Unit) {
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
            items(cities) { city ->
                CityRow(city = city, onCityClick = onCityClick)
            }
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
