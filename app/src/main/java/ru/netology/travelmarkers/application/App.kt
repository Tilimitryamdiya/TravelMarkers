package ru.netology.travelmarkers.application

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import ru.netology.travelmarkers.BuildConfig

class App : Application() {
    private val MAPKIT_API_KEY = BuildConfig.API_KEY

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(MAPKIT_API_KEY)
    }
}