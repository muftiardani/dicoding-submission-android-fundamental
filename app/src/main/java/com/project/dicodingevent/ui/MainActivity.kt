package com.project.dicodingevent.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.project.dicodingevent.R
import com.project.dicodingevent.data.local.datastore.SettingPreferences
import com.project.dicodingevent.data.local.datastore.dataStore
import com.project.dicodingevent.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    companion object {
        private val NAVIGATION_ITEMS = setOf(
            R.id.navigation_home,
            R.id.navigation_upcoming,
            R.id.navigation_finished,
            R.id.navigation_favorite,
            R.id.navigation_setting
        )

        private val HIDDEN_ACTION_BAR_DESTINATIONS = setOf(
            R.id.navigation_favorite,
            R.id.navigation_setting
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadThemeSetting()
        setupViews()
        setupNavigation()
    }

    private fun setupViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupNavigation() {
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(NAVIGATION_ITEMS)

        setupActionBarVisibility()
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun setupActionBarVisibility() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.apply {
                if (destination.id in HIDDEN_ACTION_BAR_DESTINATIONS) {
                    hide()
                } else {
                    show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                navigateToSearch()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToSearch() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun loadThemeSetting() {
        val pref = SettingPreferences.getInstance(application.dataStore)
        lifecycleScope.launch {
            val isDarkModeActive = pref.getThemeSetting().first()
            val nightMode = if (isDarkModeActive) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }
    }
}