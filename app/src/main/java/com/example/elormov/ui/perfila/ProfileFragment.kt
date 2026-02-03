package com.example.elormov.ui.perfila

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.elormov.R
import com.example.elormov.databinding.FragmentProfileBinding
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.login.MainActivity
import com.example.elormov.ui.login.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

private val DARK_MODE_KEY = booleanPreferencesKey("DarkMode")
private val LANGUAGE_KEY = stringPreferencesKey("SelectedLanguage")
class ProfileFragment : Fragment() {

	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!
	private var selectedLanguage = "es"
	private lateinit var user: User
	private lateinit var sharedViewModel: SharedViewModel


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		initUser()
		observeDarkMode()
		observeLanguage()
		initListeners()
	}

	@SuppressLint("SetTextI18n")
	private fun initUI() {
		binding.tvName.text = this.user.name + " " + this.user.lastName
		binding.tvEmail.text = this.user.mail
		binding.tvRole.text = this.user.type.name.uppercase()
		Glide.with(this)
			.load(user.argazkiaUrl)
			.placeholder(R.drawable.profile_placeholder)
			.error(R.drawable.profile_placeholder)
			.circleCrop()
			.into(binding.ivProfile)
		binding.tvInfo.text = "DNI: " + this.user.dni + "\n" +
				"Dirección: " + this.user.direccion + "\n" +
				"Teléfono: " + this.user.telefono1

	}

	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			initUI()
		}
	}

	//Cuando seleccionas el idioma cambia el color del texto para destacarlo
	private fun updateUI() {
		if (selectedLanguage == "es") {
			binding.tvSpanish.setTextColor(resources.getColor(R.color.textPrimary, null))
			binding.tvEnglish.setTextColor(resources.getColor(R.color.textSecondary, null))
			binding.tvBasque.setTextColor(resources.getColor(R.color.textSecondary, null))
		} else if (selectedLanguage == "en") {
			binding.tvSpanish.setTextColor(resources.getColor(R.color.textSecondary, null))
			binding.tvEnglish.setTextColor(resources.getColor(R.color.textPrimary, null))
			binding.tvBasque.setTextColor(resources.getColor(R.color.textSecondary, null))
		} else {
			binding.tvSpanish.setTextColor(resources.getColor(R.color.textSecondary, null))
			binding.tvEnglish.setTextColor(resources.getColor(R.color.textSecondary, null))
			binding.tvBasque.setTextColor(resources.getColor(R.color.textPrimary, null))
		}
	}

	//Aplica el modo oscuro si esta activado en SettingValue
	private fun observeDarkMode() {
		viewLifecycleOwner.lifecycleScope.launch {
			requireContext().dataStore.data
				.map { it[DARK_MODE_KEY] ?: false }
				.collect { isDarkMode ->
					// Solo actualizamos el estado visual del Switch sin disparar el listener
					binding.swDarkMode.setOnCheckedChangeListener(null)
					binding.swDarkMode.isChecked = isDarkMode

					// IMPORTANTE: Solo llamar a setDefaultNightMode si el modo actual es distinto
					val currentMode = AppCompatDelegate.getDefaultNightMode()
					val expectedMode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

					if (currentMode != expectedMode) {
						AppCompatDelegate.setDefaultNightMode(expectedMode)
					}

					// Volvemos a poner el listener después de actualizar el estado visual
					setupDarkModeListener()
				}
		}
	}

	private fun setupDarkModeListener() {
		binding.swDarkMode.setOnCheckedChangeListener { _, isChecked ->
			viewLifecycleOwner.lifecycleScope.launch {
				saveDarkMode(isChecked)
				// Al guardar, el collect de observeDarkMode se encargará de aplicar el modo
			}
		}
	}

	private fun observeLanguage() {
		viewLifecycleOwner.lifecycleScope.launch {
			requireContext().dataStore.data
				.map { it[LANGUAGE_KEY] ?: "es" } // "es" por defecto
				.collect { lang ->
					selectedLanguage = lang
					updateUI() // Aquí es donde se aplica el estilo visual (negritas/colores)
				}
		}
	}

	private fun initListeners() {
		//Activa desactiva el darkmode y lo guarda
		binding.swDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
			viewLifecycleOwner.lifecycleScope.launch {
				saveDarkMode(isChecked)
			}
		}


		binding.llSpanish.setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				saveLanguage("es")
				updateLanguage(requireContext(), "es")
			}
		}

		binding.llEnglish.setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				saveLanguage("en")
				updateLanguage(requireContext(), "en")
			}
		}

		binding.llBasque.setOnClickListener {
			viewLifecycleOwner.lifecycleScope.launch {
				saveLanguage("eu")
				updateLanguage(requireContext(), "eu")
			}
		}


		//Vuelve al login
		binding.btnLogout.setOnClickListener {
			val intent = Intent(requireContext(), MainActivity::class.java)
			intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
			startActivity(intent)
			activity?.finish()
		}
	}

	//guarda si el darkmode esta activado en SettingValue
	private suspend fun saveDarkMode(value: Boolean) {
		requireContext().dataStore.edit {
			it[DARK_MODE_KEY] = value
		}
	}
	private suspend fun saveLanguage(value: String) {
		requireContext().dataStore.edit {
			it[LANGUAGE_KEY] = value
		}
	}

	fun updateLanguage(context: Context, languageCode: String) {
		val locale = Locale(languageCode)
		Locale.setDefault(locale)

		val config = context.resources.configuration
		config.setLocale(locale)
		context.resources.updateConfiguration(config, context.resources.displayMetrics)

		activity?.recreate()
		updateUI()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}