package com.example.android_meteo_app.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ForecastApi
