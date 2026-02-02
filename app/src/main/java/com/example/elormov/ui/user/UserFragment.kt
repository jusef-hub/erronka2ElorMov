package com.example.elormov.ui.user


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
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
import com.example.elormov.domain.model.CyclesResponse
import com.example.elormov.domain.model.TeacherResponse
import com.example.elormov.domain.model.User
import com.example.elormov.domain.model.UserResponse
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.user.adapter.UserAdapter
import com.example.elormov.ui.user.viewmodels.AlumViewModel
import com.example.elormov.ui.user.viewmodels.CyclesState
import com.example.elormov.ui.user.viewmodels.CyclesViewModel
import com.example.elormov.ui.user.viewmodels.AlumState
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
	private var cycleList = mutableListOf<String>()
	private lateinit var user: User
	private lateinit var selectionCycles: String
	private lateinit var selectionSemesters: String
	private lateinit var sharedViewModel: SharedViewModel
	private val alumViewModel: AlumViewModel by viewModels()
	private val cyclesViewModel: CyclesViewModel by viewModels()
	private val teacherViewModel: TeacherViewModel by viewModels()
	private val all by lazy { getString(R.string.all) }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		selectionCycles = getString(R.string.all)
		selectionSemesters = getString(R.string.all)
		initComponents()
		initUser()
	}


	/* --- Parte de Cycles --- */
	private fun initUIStateCycles() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				cyclesViewModel.state.collect { state ->
					Log.i("Cycles", state.toString())
					when (state) {
						is CyclesState.Error -> errorState(state.error)
						CyclesState.Loading -> loadingState()
						is CyclesState.Success -> successStateCycles(state.users)
					}
				}
			}
		}
	}

	private fun successStateCycles(cycles: List<CyclesResponse>) {
		binding.pb.visibility = View.GONE
		cycleList = mutableListOf()
		cycleList.add(getString(R.string.all))
		for (cycle in cycles) {
			cycleList.add(cycle.name)
		}
		initFilters()
	}


	/* --- Hace un filtro con los ciclos y el curso --- */
	private fun initFilters() {
		val adapterCycle = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_spinner_item,
			cycleList
		)
		adapterCycle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.spCycle.adapter = adapterCycle
		binding.spCycle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

				selectionCycles = parent.getItemAtPosition(position).toString()
				filtersAlums()
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {}

		}

		val adapterSemester = ArrayAdapter(
			requireContext(),
			android.R.layout.simple_spinner_item,
			listOf(getString(R.string.all),"1","2")
		)
		adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
		binding.spSemester.adapter = adapterSemester
		binding.spSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

				selectionSemesters = parent.getItemAtPosition(position).toString()
				filtersAlums()
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {}

		}
	}
	private fun filtersAlums() {
		if (userList.isEmpty()) return

		//Filtra los alumnos de uno en uno haciendo que cumplan las condiciones
		filterList = userList.filter { user ->
			//Condicion1
			//Si en el desplegable estaseleccionado todos o el ciclo seleccionado es igual que el del alumno
			//devuelve true si no false
			val matchCycle =
				selectionCycles == all || user.cycle?.name == selectionCycles

			//Condicion2
			//Si en el desplegable estaseleccionado todos o el semestre seleccionado es igual que el del alumno
			//devuelve true si no false
			val matchSemester =
				selectionSemesters == all || user.semester.toString() == selectionSemesters

			//Coprueba si el usuario cumple ambas condiciones
			//Si las cumple mete el usuario en la lista filtrada, si no lo descarta
			matchCycle && matchSemester
		}.toMutableList()

		userAdapter.updateList(filterList)
	}



	/* --- Consigue los alumnos --- */
	private fun initUIStateAlums() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				alumViewModel.state.collect { state ->
					when (state) {
						is AlumState.Error -> errorState(state.error)
						AlumState.Loading -> loadingState()
						is AlumState.Success -> successStateAlums(state.alums)
					}
				}
			}
		}
	}

	private fun successStateAlums(users: List<UserResponse>) {
		binding.pb.visibility = View.GONE
		userList = mutableListOf<User>()

		for (user in users) {
			val newUser = User(
				user.alumno.userID,
				user.alumno.mail,
				user.alumno.name,
				user.alumno.lastName,
				user.alumno.dni,
				user.alumno.direccion,
				user.alumno.telefono1,
				user.alumno.argazkiaUrl,
				user.alumno.tipo,
				user.ciclo,
				user.curso
			)
			userList.add(newUser)
		}
		filterList = userList.toMutableList()
		filtersAlums()
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
		when (user.type.id) {
			3 -> {
				binding.llSearch.visibility = View.GONE
				binding.clFilter.visibility = View.VISIBLE
				alumViewModel.getAlums(user.userID)
				cyclesViewModel.getCycles()
				initUIStateAlums()
				initUIStateCycles()
			}
			4 -> {
				binding.llSearch.visibility = View.VISIBLE
				binding.clFilter.visibility = View.GONE
				teacherViewModel.getTeachers()
				initUIStateTeachers()
			}
		}
	}


	/* --- Inicia el adapter --- */
	private fun initComponents() {
		userAdapter = UserAdapter(onItemSelected = {onItemSelected(it)})
		binding.rvUsers.layoutManager = GridLayoutManager(context,1)
		binding.rvUsers.adapter = userAdapter

		binding.etSearch.addTextChangedListener {
			filtersTeachers()
		}
	}



	/* --- Cuando clicas muestra un dialog con informacion que depende del tipo de usuario --- */
	@SuppressLint("SetTextI18n")
	private fun onItemSelected(user: User) {
		when(user.type.id) {
			3 -> {
				//Tipo de usuario seleccionado: Profesor
				//Muestra un dialog con los horarios del profesor
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


