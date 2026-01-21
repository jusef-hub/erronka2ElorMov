package com.example.elormov.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.elormov.R
import com.example.elormov.databinding.ActivityMainBinding
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.HomeActivity
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.perfila.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val DARK_MODE_KEY = booleanPreferencesKey("DarkMode")
private val LANGUAGE_KEY = stringPreferencesKey("SelectedLanguage")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val loginViewModel: LoginViewModel by viewModels()
	private lateinit var viewModel: SharedViewModel
	private lateinit var user: User

	override fun onCreate(savedInstanceState: Bundle?) {
		initLangue()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		viewModel = ViewModelProvider(this)[SharedViewModel::class.java]

		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		//Deja la imagen del logo a la escala necesaria al iniciar la animación
		binding.ivLogo.scaleY = 0.1f
		binding.ivLogo.scaleX = 0.1f

		initDarkMode()
		initListeners()
		initUIState()

		//Espera 0,5 segundos para iniciar la animacion
		binding.ivLogo.postDelayed({
			startAnimation()
		}, 500)
	}

	private fun initUIState() {

		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				loginViewModel.state.collect {
					//Este es el codigo que se ejecuta cada vez que cambia el ViewModel
					when (it) {
						LoginState.Loading -> loadingState()
						is LoginState.Error -> errorState(it)
						is LoginState.Success -> successState(it)

					}
				}
			}
		}
	}

	private fun loadingState() {
		//Dibujar loading
	}

	private fun errorState(it: LoginState.Error) {
		//Dibujar errordibuja
		Log.i("LOGIN", it.error)
	}

	private fun successState(it: LoginState.Success) {
		//Dibujar success
		Log.i("LOGIN", it.success.name)
		user = User(
			it.success.userID,
			it.success.mail,
			it.success.name,
			it.success.lastName,
			it.success.dni,
			it.success.direccion,
			it.success.telefono1,
			it.success.argazkiaUrl,
			it.success.tipo
		)
		viewModel.user.value = user
		val intent = Intent(this, HomeActivity::class.java)

		startActivity(intent)
		finish()
	}

	private fun initListeners() {
		binding.btnLogin.setOnClickListener {
			val user = binding.etUsername.text.toString()
			val pass = binding.etPassword.text.toString()
			loginViewModel.getAuth(user, pass)
		}
	}

	private fun initDarkMode() {
		lifecycleScope.launch {
			dataStore.data
				.map { it[DARK_MODE_KEY] ?: false }
				.collect { isDarkMode ->
					val mode = if (isDarkMode)
						AppCompatDelegate.MODE_NIGHT_YES
					else
						AppCompatDelegate.MODE_NIGHT_NO

					if (AppCompatDelegate.getDefaultNightMode() != mode) {
						AppCompatDelegate.setDefaultNightMode(mode)
					}
				}
		}
	}

	private fun initLangue() {
		lifecycleScope.launch {
			val lang = dataStore.data
				.map { it[LANGUAGE_KEY] ?: "es" }
				.first()

			val appLocale = LocaleListCompat.forLanguageTags(lang)
			AppCompatDelegate.setApplicationLocales(appLocale)
		}
	}

	private fun startAnimation() {
		// Animacion de girar el logo
		val rotation = ObjectAnimator.ofFloat(binding.ivLogo, View.ROTATION, 0f, 360f)
		rotation.duration = 2000

		// Animacion de agrandar el logo
		val scaleX = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_X, 0.1f, 1f)
		val scaleY = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_Y, 0.1f, 1f)
		scaleX.duration = 2000
		scaleY.duration = 2000

		//Crea un AnimatorSet() para poder ejecutar las dos animaciones a la vez
		val animatorSet = AnimatorSet()
		animatorSet.playTogether(rotation, scaleX, scaleY)
		animatorSet.start()
	}
}