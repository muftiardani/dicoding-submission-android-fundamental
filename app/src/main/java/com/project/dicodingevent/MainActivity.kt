package com.project.dicodingevent

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.dicodingevent.data.preferences.DarkModePreferences
import com.project.dicodingevent.data.preferences.dataStore
import com.project.dicodingevent.databinding.ActivityMainBinding
import com.project.dicodingevent.ui.setting.SettingViewModel
import com.project.dicodingevent.ui.setting.SettingViewModelFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSION_PREF = "permission_pref"
        private const val NOTIFICATION_PERMISSION_KEY = "notification_permission_granted"
    }

    // View Binding
    private lateinit var binding: ActivityMainBinding

    // Preferences
    private lateinit var sharedPreferences: SharedPreferences

    // ViewModel
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewBinding()
        setupPreferences()
        checkAndRequestNotificationPermission()
        setupDarkMode()
        setupNavigation()
    }

    // Setup Methods
    private fun setupViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupPreferences() {
        sharedPreferences = getSharedPreferences(PERMISSION_PREF, Context.MODE_PRIVATE)
    }

    private fun setupDarkMode() {
        val pref = DarkModePreferences.getInstance(application.dataStore)
        settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(pref)
        )[SettingViewModel::class.java]

        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            updateTheme(isDarkModeActive)
        }
    }

    private fun updateTheme(isDarkModeActive: Boolean) {
        val mode = if (isDarkModeActive) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragmentActivityMain) as NavHostFragment
        val navController = navHostFragment.navController
        val navView: BottomNavigationView = binding.navView

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigationHome,
                R.id.navigationUpcoming,
                R.id.navigationFinished,
                R.id.detailFragment,
                R.id.navigationFavorite
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setupDestinationChangeListener(navController)
    }

    private fun setupDestinationChangeListener(navController: androidx.navigation.NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbarTitle(destination.id)
        }
    }

    private fun updateToolbarTitle(destinationId: Int) {
        supportActionBar?.apply {
            title = when (destinationId) {
                R.id.navigationHome -> "Home"
                R.id.navigationUpcoming -> "Upcoming Events"
                R.id.navigationFinished -> "Finished Events"
                R.id.navigationFavorite -> "Favorite Events"
                R.id.detailFragment -> {
                    setDisplayHomeAsUpEnabled(true)
                    "Detail Event"
                }
                R.id.settingFragment -> {
                    setDisplayHomeAsUpEnabled(true)
                    "Pengaturan"
                }
                else -> "App Title"
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        val isPermissionGranted = sharedPreferences.getBoolean(NOTIFICATION_PERMISSION_KEY, false)
        if (Build.VERSION.SDK_INT >= 33 && !isPermissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Menu Methods
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.actionSettings -> {
                findNavController(R.id.navHostFragmentActivityMain)
                    .navigate(R.id.settingFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragmentActivityMain)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Permission Handler
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val message = if (isGranted) {
            sharedPreferences.edit()
                .putBoolean(NOTIFICATION_PERMISSION_KEY, true)
                .apply()
            "Notifications permission granted"
        } else {
            "Notifications permission rejected"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}