package com.example.elormov.ui.user

import android.R
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.elormov.databinding.ActivityUserDetailsBinding
import com.example.elormov.databinding.FragmentUserBinding
import com.example.elormov.domain.model.CyclesResponse
import com.example.elormov.domain.model.User
import com.example.elormov.domain.model.UserResponse
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.user.adapter.UserAdapter
import com.example.elormov.ui.user.viewmodels.CyclesState
import com.example.elormov.ui.user.viewmodels.CyclesViewModel
import com.example.elormov.ui.user.viewmodels.UserState
import com.example.elormov.ui.user.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {

	private var _binding: FragmentUserBinding? = null
	private val binding get() = _binding!!
	private lateinit var userAdapter: UserAdapter
	private var cycleList = mutableListOf<String>()
	private lateinit var user: User
	private lateinit var sharedViewModel: SharedViewModel
	private val userViewModel: UserViewModel by viewModels()
	private val cyclesViewModel: CyclesViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		initUIState()
		initUIStateCycles()
		initComponents()
		initUser()
	}

	private fun initUIStateCycles() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				cyclesViewModel.state.collect { state ->
					lifecycleScope.launch {
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
	}

	private fun successStateCycles(cycles: List<CyclesResponse>) {
		binding.pb.visibility = View.GONE
		cycleList = mutableListOf<String>()
		cycleList.add("Guztiak")
		for (cycle in cycles) {
			Log.i("Cycles", cycle.name)
			cycleList.add(cycle.name)
		}
		initFilters()
	}

	private fun initFilters() {
		val adapterCycle = ArrayAdapter(
			requireContext(),
			R.layout.simple_spinner_item,
			cycleList
		)
		adapterCycle.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
		binding.spCycle.adapter = adapterCycle
		Log.i("Cycles esto caca", cycleList.toString())
		binding.spCycle.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
				val selection = parent.getItemAtPosition(position).toString()
				Toast.makeText(requireContext(), "Seleccionaste: $selection", Toast.LENGTH_SHORT).show()
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {}

		}

		val adapterSemester = ArrayAdapter(
			requireContext(),
			R.layout.simple_spinner_item,
			listOf("Guztiak","1","2")
		)
		adapterSemester.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
		binding.spSemester.adapter = adapterSemester
		Log.i("Cycles esto caca", cycleList.toString())
		binding.spSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
				val selection = parent.getItemAtPosition(position).toString()
				Toast.makeText(requireContext(), "Seleccionaste: $selection", Toast.LENGTH_SHORT).show()
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {}

		}
	}

	private fun initUIState() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				userViewModel.state.collect { state ->
					lifecycleScope.launch {
						when (state) {
							is UserState.Error -> errorState(state.error)
							UserState.Loading -> loadingState()
							is UserState.Success -> successState(state.users)
						}
					}
				}
			}
		}
	}

	private fun successState(users: List<UserResponse>) {
		binding.pb.visibility = View.GONE
		val userList = mutableListOf<User>()

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
			Log.i("Usersss", newUser.toString())
			userList.add(newUser)
		}
		userAdapter.updateList(userList)
	}
	private fun loadingState() {
		//dibujar loading
		binding.pb.visibility = View.VISIBLE
	}

	private fun errorState(error: String) {
		//error message
		Log.i("Scheduleeeeeeeeeeeeeee", error)
		binding.pb.visibility = View.GONE
	}

	private fun initUI() {
		if (user.type.name == "profesor") {
			binding.llSearch.visibility = View.GONE
			binding.clFilter.visibility = View.VISIBLE
			lifecycleScope.launch {
				repeatOnLifecycle(Lifecycle.State.STARTED) {
					//Este es el codigo que se va a realizar
					//cada vez que la lista se actualize en el ViewModel
					//userAdapter.updateList()
				}
			}
		} else if (user.type.name == "alumno") {
			binding.llSearch.visibility = View.VISIBLE
			binding.clFilter.visibility = View.GONE
		}

	}

	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			userViewModel.getUsers(user.userID)
			cyclesViewModel.getCycles()
			initUI()
		}
	}

	private fun initComponents() {
		userAdapter = UserAdapter(onItemSelected = {
			onItemSelected(it)
		})
		binding.rvUsers.layoutManager = GridLayoutManager(context,1)
		binding.rvUsers.adapter = userAdapter
	}

	private fun onItemSelected(user: User) {
		val userDetailsBinding = ActivityUserDetailsBinding.inflate(layoutInflater)

		val dialog = Dialog(requireContext())
		dialog.setContentView(userDetailsBinding.root)
		dialog.show()
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentUserBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

}


