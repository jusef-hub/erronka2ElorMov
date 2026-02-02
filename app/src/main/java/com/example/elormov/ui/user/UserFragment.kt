package com.example.elormov.ui.user


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.elormov.R
import com.example.elormov.databinding.ActivityUserDetailsBinding
import com.example.elormov.databinding.FragmentUserBinding
import com.example.elormov.domain.model.TeacherResponse
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.user.adapter.UserAdapter
import com.example.elormov.ui.user.viewmodels.TeacherState
import com.example.elormov.ui.user.viewmodels.TeacherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {

	private var _binding: FragmentUserBinding? = null
	private val binding get() = _binding!!
	private lateinit var userAdapter: UserAdapter
	private var userList = mutableListOf<User>()
	private var filterList = mutableListOf<User>()
	private lateinit var user: User
	private lateinit var selectionCycles: String
	private lateinit var selectionSemesters: String
	private lateinit var sharedViewModel: SharedViewModel
	private val teacherViewModel: TeacherViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		selectionCycles = getString(R.string.all)
		selectionSemesters = getString(R.string.all)
		initComponents()
		initUser()
	}

	/* --- Consigue los profesores --- */
	private fun initUIStateTeachers() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				teacherViewModel.state.collect { state ->
					when (state) {
						is TeacherState.Error -> errorState(state.error)
						TeacherState.Loading -> loadingState()
						is TeacherState.Success -> successStateTeachers(state.teachers)
					}
				}
			}
		}
	}

	private fun successStateTeachers(users: List<TeacherResponse>) {
		binding.pb.visibility = View.GONE
		userList = mutableListOf()
		for (user in users) {
			val newUser = User(
				user.id,
				user.mail,
				user.name,
				user.lastName,
				null,
				null,
				null,
				user.image,
				user.type,
				null,
				null,
			)
			userList.add(newUser)
		}
		filterList = userList.toMutableList()
		filtersTeachers()
	}

	//Filtra los profesores segun el nombre y apellido
	private fun filtersTeachers() {
		if (userList.isEmpty()) return

		if (binding.etSearch.text.isNotEmpty()) {
			//Filtra los profesores de uno en uno
			filterList = userList.filter { user ->

				val nameLastName = "${user.name} ${user.lastName}"

				//Comprueba si el editText contiene el nombre y apellido del profesor
				//Si lo contiene devuelve true y lo mete en la lista filtrada
				//Si no devuelve false y lo descarta
				nameLastName.lowercase().contains(binding.etSearch.text.toString().lowercase())

			}.toMutableList()
			userAdapter.updateList(filterList)
		} else {
			userAdapter.updateList(userList)
		}
	}


	//Enseña el progressBar mientras carga los datos
	private fun loadingState() {
		binding.pb.visibility = View.VISIBLE
	}

	//Si da error quita el progressBar y muestra el error por consola
	private fun errorState(error: String) {
		//error message
		Log.i("Scheduleeeeeeeeeeeeeee", error)
		binding.pb.visibility = View.GONE
	}


	/* --- Inicializa el usuario --- */
	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			initUI()
		}
	}



	/* --- Cambia la UI dependiendo del tipo de usuario --- */
	private fun initUI() {
		binding.llSearch.visibility = View.VISIBLE
		binding.clFilter.visibility = View.GONE
		teacherViewModel.getTeachers()
		initUIStateTeachers()

		binding.etSearch.addTextChangedListener {
			filtersTeachers()
		}
	}


	/* --- Inicia el adapter --- */
	private fun initComponents() {
		userAdapter = UserAdapter(onItemSelected = {onItemSelected(it)})
		binding.rvUsers.layoutManager = GridLayoutManager(context,1)
		binding.rvUsers.adapter = userAdapter
	}



	/* --- Cuando clicas muestra un dialog con informacion que depende del tipo de usuario --- */
	@SuppressLint("SetTextI18n")
	private fun onItemSelected(user: User) {
		when(user.type.id) {
			3 -> {
				//Tipo de usuario seleccionado: Profesor
				//Muestra un dialog con los horarios del profesor
				// PROFESOR → mostrar horarios

				val dialog = TeacherScheduleDialogFragment().apply {
					arguments = Bundle().apply {
						putInt("teacher_id", user.userID)
					}
				}
				dialog.show(parentFragmentManager, "TeacherScheduleDialog")
			}
			4 -> {
				//Tipo de usuario seleccionado: Alumno
				//Muestra un dialog con la informacion del alumno
				//Nombre, Apellidos, Curso, Ciclo y foto de perfil de tenerla
				val userDetailsBinding = ActivityUserDetailsBinding.inflate(layoutInflater)

				val dialog = Dialog(requireContext())
				dialog.setContentView(userDetailsBinding.root)
				dialog.show()

				Glide.with(this)
					.load(user.argazkiaUrl)
					.placeholder(R.drawable.profile_placeholder)
					.error(R.drawable.profile_placeholder)
					.circleCrop()
					.into(userDetailsBinding.ivUserImg)
				userDetailsBinding.tvUsername.text = "${user.name} ${user.lastName}\n${user.semester} ${user.cycle?.name}"
			}
		}


	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentUserBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}