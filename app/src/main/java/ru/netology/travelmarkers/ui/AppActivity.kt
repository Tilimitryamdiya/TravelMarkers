package ru.netology.travelmarkers.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.netology.travelmarkers.R
import ru.netology.travelmarkers.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity(R.layout.activity_app) {

    lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppBinding.inflate(layoutInflater)

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        navView.setupWithNavController(navController)
    }
}