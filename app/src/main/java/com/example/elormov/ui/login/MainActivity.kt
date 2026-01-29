package com.example.elormov.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.elormov.R
import com.example.elormov.databinding.ActivityMainBinding
import com.example.elormov.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsDB")
private val DARK_MODE_KEY = booleanPreferencesKey("DarkMode")
private val LANGUAGE_KEY = stringPreferencesKey("SelectedLanguage")
private val user = stringPreferencesKey("User")
private val pass = stringPreferencesKey("Pass")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val loginViewModel: LoginViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		initLangue()
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		//Deja la imagen del logo a la escala necesaria al iniciar la animación
		binding.ivLogo.scaleY = 0.1f
		binding.ivLogo.scaleX = 0.1f

		loadUser()
		initDarkMode()
		initListeners()

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
		binding.pb.visibility = View.VISIBLE
		binding.tvError.visibility = View.GONE
	}

	private fun errorState(it: LoginState.Error) {
		//Dibujar errordibuja
		Log.i("LOGIN", it.error)
		binding.pb.visibility = View.GONE
		binding.tvError.text = it.error
		binding.tvError.visibility = View.VISIBLE
	}

	private fun successState(it: LoginState.Success) {
		//Dibujar success
		Log.i("LOGIN", it.success.name)
		val user = it.success
		if (user.tipo.id == 1 || user.tipo.id == 2) {
			binding.tvError.setText(R.string.access_denied)
			binding.pb.visibility = View.GONE
			binding.tvError.visibility = View.VISIBLE
		} else {
			val intent = Intent(this, HomeActivity::class.java)
			intent.putExtra("USER_NAME", binding.etUsername.text.toString())
			intent.putExtra("USER_PASS", binding.etPassword.text.toString())
			intent.putExtra("USER_ID", user.userID)
			intent.putExtra("USER_MAIL", user.mail)
			intent.putExtra("NAME", user.name)
			intent.putExtra("USER_LASTNAME", user.lastName)
			intent.putExtra("USER_DNI", user.dni)
			intent.putExtra("USER_ADDRESS", user.direccion)
			intent.putExtra("USER_PHONE1", user.telefono1)
			intent.putExtra("USER_IMAGE", user.argazkiaUrl)
			intent.putExtra("USER_TYPE_ID", user.tipo.id)
			intent.putExtra("USER_TYPE_NAME", user.tipo.name)

			binding.tvError.visibility = View.GONE

			startActivity(intent)
			finish()
		}
	}

	private fun initListeners() {
		binding.btnLogin.setOnClickListener {
			val user = binding.etUsername.text.toString()
			val pass = binding.etPassword.text.toString()
			loginViewModel.getAuth(user, pass)
			initUIState()
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

	private fun loadUser() {
		lifecycleScope.launch {
			val userText = dataStore.data
				.map { it[user] ?: "" }
				.first()
			binding.etUsername.setText(userText)

			val passText = dataStore.data
				.map { it[pass] ?: "" }
				.first()
			binding.etPassword.setText(passText)
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