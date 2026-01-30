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
	private var userList = mutableListOf<User>()
	private var filterList = mutableListOf<User>()
	private var cycleList = mutableListOf<String>()
	private lateinit var user: User
	private lateinit var selectionCycles: String
	private lateinit var selectionSemesters: String
	private lateinit var sharedViewModel: SharedViewModel
	private val userViewModel: UserViewModel by viewModels()
	private val cyclesViewModel: CyclesViewModel by viewModels()
	private val all by lazy { getString(R.string.all) }

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		selectionCycles = getString(R.string.all)
		selectionSemesters = getString(R.string.all)
		initUIState()
		initUIStateCycles()
		initComponents()
		initUser()
	}

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
				applyFilters()
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
				applyFilters()
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {}

		}
	}
	private fun applyFilters() {
		if (userList.isEmpty()) return
		filterList = userList.filter { user ->
			val matchCycle =
				selectionCycles == all || user.cycle?.name == selectionCycles

			val matchSemester =
				selectionSemesters == all || user.semester.toString() == selectionSemesters

			matchCycle && matchSemester
		}.toMutableList()

		userAdapter.updateList(filterList)
	}

	private fun initUIState() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				userViewModel.state.collect { state ->
					when (state) {
						is UserState.Error -> errorState(state.error)
						UserState.Loading -> loadingState()
						is UserState.Success -> successState(state.users)
					}
				}
			}
		}
	}

	private fun successState(users: List<UserResponse>) {
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
		applyFilters()
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
		when (user.type.id) {
			3 -> {
				binding.llSearch.visibility = View.GONE
				binding.clFilter.visibility = View.VISIBLE
				userViewModel.getAlums(user.userID)
				cyclesViewModel.getCycles()
			}
			4 -> {
				binding.llSearch.visibility = View.VISIBLE
				binding.clFilter.visibility = View.GONE
			}
		}
	}

	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			initUI()
		}
	}

	private fun initComponents() {
		userAdapter = UserAdapter(onItemSelected = {onItemSelected(it)})
		binding.rvUsers.layoutManager = GridLayoutManager(context,1)
		binding.rvUsers.adapter = userAdapter
	}

	@SuppressLint("SetTextI18n")
	private fun onItemSelected(user: User) {
		val userDetailsBinding = ActivityUserDetailsBinding.inflate(layoutInflater)

		val dialog = Dialog(requireContext())
		dialog.setContentView(userDetailsBinding.root)
		dialog.show()

		userDetailsBinding.tvUsername.text = "${user.name} ${user.lastName}\n${user.semester} ${user.cycle?.name}"
		Glide.with(this)
			.load(user.argazkiaUrl)
			.placeholder(R.drawable.profile_placeholder)
			.error(R.drawable.profile_placeholder)
			.circleCrop()
			.into(userDetailsBinding.ivUserImg)
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


