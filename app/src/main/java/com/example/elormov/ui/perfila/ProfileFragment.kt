package com.example.elormov.ui.perfila

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.elormov.R
import com.example.elormov.databinding.FragmentProfileBinding
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.login.MainActivity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsDB")
private val DARK_MODE_KEY = booleanPreferencesKey("DarkMode")
private val LANGUAGE_KEY = stringPreferencesKey("SelectedLanguage")
class ProfileFragment : Fragment() {

	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!
	private var selectedLanguage = "es"
	private lateinit var user: User
	private lateinit var viewModel: SharedViewModel


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		initListeners()
		//initUser()
		observeDarkMode()
		observeLanguage()
	}

	/*private fun initUser() {
		viewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			binding.tvName.text = user.name
		}
	}*/

	private fun updateUI() {
		if (selectedLanguage == "es") {
			binding.tvSpanish.setTextColor(resources.getColor(R.color.textPrimary, null))
			binding.tvEnglish.setTextColor(resources.getColor(R.color.textSecondary, null))
			binding.tvBasque.setTextColor(resources.getColor(R.color.textSecondary, null))
			Log.i("LANGUAGE", selectedLanguage)
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

	private fun observeDarkMode() {
		viewLifecycleOwner.lifecycleScope.launch {
			requireContext().dataStore.data
				.map { it[DARK_MODE_KEY] ?: false }
				.collect { isDarkMode ->

					// Evita que el listener se dispare solo
					binding.swDarkMode.setOnCheckedChangeListener(null)
					binding.swDarkMode.isChecked = isDarkMode

					//Comprueba si el usuario tenia el darkmode activado
					//y lo activa de ser asi
					AppCompatDelegate.setDefaultNightMode(
						if (isDarkMode)
							AppCompatDelegate.MODE_NIGHT_YES
						else
							AppCompatDelegate.MODE_NIGHT_NO
					)

					initListeners()
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
			startActivity(intent)
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