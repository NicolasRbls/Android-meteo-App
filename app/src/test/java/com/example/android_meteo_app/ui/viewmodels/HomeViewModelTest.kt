package com.example.android_meteo_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android_meteo_app.domain.City
import com.example.android_meteo_app.domain.LocationTracker
import com.example.android_meteo_app.domain.WeatherRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: HomeViewModel
    private val weatherRepository: WeatherRepository = mockk()
    private val locationTracker: LocationTracker = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { weatherRepository.getFavoriteCities() } returns flowOf(emptyList())
        coEvery { weatherRepository.getWeather(any()) } returns Result.success(
            com.example.android_meteo_app.domain.WeatherInfo(
                city = City(1, "Test", 0.0, 0.0, "Test", null),
                currentTemperature = 10.0,
                currentCondition = com.example.android_meteo_app.domain.WeatherCondition.SUNNY,
                hourlyForecast = emptyList(),
                minTemperature = 5.0,
                maxTemperature = 15.0,
                windSpeed = 10.0
            )
        )
        coEvery { locationTracker.getCurrentLocation() } returns null
        viewModel = HomeViewModel(weatherRepository, locationTracker)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChange updates searchResults and isSearching`() = runTest {
        val query = "Paris"
        val mockCities = listOf(City(1, "Paris", 0.0, 0.0, "France", null))
        coEvery { weatherRepository.searchCity(query) } returns Result.success(mockCities)

        viewModel.onSearchQueryChange(query)
        advanceUntilIdle()

        assertEquals(query, viewModel.uiState.value.searchQuery)
        assertEquals(mockCities, viewModel.uiState.value.searchResults)
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `onSearchQueryChange debounces search requests`() = runTest {
        val query1 = "Pa"
        val query2 = "Pari"
        val query3 = "Paris"
        val mockCities = listOf(City(1, "Paris", 0.0, 0.0, "France", null))
        coEvery { weatherRepository.searchCity(any()) } returns Result.success(mockCities)

        viewModel.onSearchQueryChange(query1)
        advanceUntilIdle() // Should not trigger search

        viewModel.onSearchQueryChange(query2)
        advanceUntilIdle() // Should not trigger search

        viewModel.onSearchQueryChange(query3)
        advanceUntilIdle() // Should trigger search after debounce

        assertEquals(query3, viewModel.uiState.value.searchQuery)
        assertEquals(mockCities, viewModel.uiState.value.searchResults)
        assertFalse(viewModel.uiState.value.isSearching)
    }

    @Test
    fun `onSearchQueryChange handles search failure`() = runTest {
        val query = "ErrorCity"
        val errorMessage = "Network error"
        coEvery { weatherRepository.searchCity(query) } returns Result.failure(Exception(errorMessage))

        viewModel.onSearchQueryChange(query)
        advanceUntilIdle()

        assertEquals(query, viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
        assertFalse(viewModel.uiState.value.isSearching)
        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains(errorMessage))
    }

    @Test
    fun `onLocationRequested updates currentUserLocation on success`() = runTest {
        val mockLocation: android.location.Location = mockk(relaxed = true) {
            every { latitude } returns 1.0
            every { longitude } returns 2.0
        }
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation

        viewModel.onLocationRequested()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSearching)
        assertEquals(mockLocation, viewModel.uiState.value.currentUserLocation)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onLocationRequested handles location failure`() = runTest {
        coEvery { locationTracker.getCurrentLocation() } returns null

        viewModel.onLocationRequested()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSearching)
        assertNull(viewModel.uiState.value.currentUserLocation)
        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains("Could not retrieve location"))
    }

    @Test
    fun `clearError clears the error message`() = runTest {
        val errorMessage = "Some error"
        coEvery { weatherRepository.searchCity(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.onSearchQueryChange("test")
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `onPermissionDenied sets permission denied error`() = runTest {
        viewModel.onPermissionDenied()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.error!!.contains("Location permissions denied"))
    }

    @Test
    fun `onLocationNavigationConsumed clears currentUserLocation`() = runTest {
        val mockLocation: android.location.Location = mockk(relaxed = true) {
            every { latitude } returns 1.0
            every { longitude } returns 2.0
        }
        coEvery { locationTracker.getCurrentLocation() } returns mockLocation

        viewModel.onLocationRequested()
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.currentUserLocation)

        viewModel.onLocationNavigationConsumed()
        assertNull(viewModel.uiState.value.currentUserLocation)
    }
}
