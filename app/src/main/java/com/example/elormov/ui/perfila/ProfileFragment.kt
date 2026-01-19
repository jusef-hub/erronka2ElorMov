package com.example.elormov.ui.perfila

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.elormov.databinding.FragmentProfileBinding
import com.example.elormov.ui.login.MainActivity
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settingsDB")
private val DARK_MODE_KEY = booleanPreferencesKey("DarkMode")
class ProfileFragment : Fragment() {

	private var _binding: FragmentProfileBinding? = null
	private val binding get() = _binding!!


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initListeners()
		observeDarkMode()
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

	private fun initListeners() {
		//Activa desactiva el darkmode y lo guarda
		binding.swDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->
			viewLifecycleOwner.lifecycleScope.launch {
				saveDarkMode(isChecked)
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