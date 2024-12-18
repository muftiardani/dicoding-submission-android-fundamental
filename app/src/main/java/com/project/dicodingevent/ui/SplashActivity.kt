package com.project.dicodingevent.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.project.dicodingevent.R
import com.project.dicodingevent.data.local.datastore.SettingPreferences
import com.project.dicodingevent.data.local.datastore.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private companion object {
        const val SPLASH_DELAY = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        loadThemeSetting()
        navigateToMain()
    }

    private fun setupView() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
    }

    private fun navigateToMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, MainActivity::class.java).also { intent ->
                startActivity(intent)
                finish()
            }
        }, SPLASH_DELAY)
    }

    private fun loadThemeSetting() {
        val pref = SettingPreferences.getInstance(application.dataStore)
        lifecycleScope.launch {
            when (pref.getThemeSetting().first()) {
                true -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}