package com.example.elormov.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.elormov.R
import com.example.elormov.databinding.ActivityHomeBinding
import com.example.elormov.domain.model.TipoUsuario
import com.example.elormov.domain.model.User
import com.example.elormov.ui.login.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private val user = stringPreferencesKey("User")
private val pass = stringPreferencesKey("Pass")
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

	private lateinit var binding: ActivityHomeBinding
	private lateinit var navController: NavController
	private val viewModel: SharedViewModel by viewModels()

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
		lifecycleScope.launch { saveUser() }
		initUI()
		initViewModel()
	}

	private suspend fun saveUser() {
		dataStore.edit {
			it[user] = intent.getStringExtra("USER_NAME") ?: ""
			it[pass] = intent.getStringExtra("USER_PASS") ?: ""
		}
	}

	private fun initViewModel() {
		val tipo = TipoUsuario(
			intent.getIntExtra("USER_TYPE_ID", 1),
			intent.getStringExtra("USER_TYPE_NAME") ?: ""
		)
		val user = User(
			intent.getIntExtra("USER_ID", 0),
			intent.getStringExtra("USER_MAIL") ?: "",
			intent.getStringExtra("NAME") ?: "",
			intent.getStringExtra("USER_LASTNAME") ?: "",
			intent.getStringExtra("USER_DNI"),
			intent.getStringExtra("USER_ADDRESS"),
			intent.getStringExtra("USER_PHONE1"),
			intent.getStringExtra("USER_IMAGE"),
			tipo,
			null,
			null
		)
		viewModel.user.value = user
	}

	private fun initUI() {
		initNav()
	}

	private fun initNav() {
		val navHost: NavHostFragment =
			supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
		navController = navHost.navController
		binding.bottomBar.setupWithNavController(navController)
	}
}