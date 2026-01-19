package com.example.elormov.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.elormov.R
import com.example.elormov.databinding.ActivityHomeBinding
import com.example.elormov.ui.perfila.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val DARK_MODE_KEY = booleanPreferencesKey("DarkMode")
class HomeActivity : AppCompatActivity() {

	private lateinit var binding: ActivityHomeBinding
	private lateinit var navController: NavController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityHomeBinding.inflate(layoutInflater)
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		enableDarkMode()
		initUI()
	}
	private fun initUI() {
		initNav()
	}

	private fun enableDarkMode() {
		lifecycleScope.launch {
			dataStore.data
				.map { it[DARK_MODE_KEY] ?: false }
				.collect { isDarkMode ->
					AppCompatDelegate.setDefaultNightMode(
						if (isDarkMode)
							AppCompatDelegate.MODE_NIGHT_YES
						else
							AppCompatDelegate.MODE_NIGHT_NO
					)
				}
		}
	}

	private fun initNav() {
		val navHost: NavHostFragment =
			supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
		navController = navHost.navController
		binding.bottomBar.setupWithNavController(navController)
	}
}