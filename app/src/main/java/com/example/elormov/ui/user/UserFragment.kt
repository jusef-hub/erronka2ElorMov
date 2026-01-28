package com.example.elormov.ui.user

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.elormov.databinding.FragmentUserBinding
import com.example.elormov.domain.model.LoginResponse
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.user.adapter.UserAdapter
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

	private var _binding: FragmentUserBinding? = null
	private val binding get() = _binding!!
	private lateinit var userAdapter: UserAdapter
	private lateinit var user: User
	private lateinit var sharedViewModel: SharedViewModel
	private val userViewModel: UserViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		initUser()
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

	private fun successState(users: List<LoginResponse>) {

	}
	private fun loadingState() {
		//dibujar loading
	}

	private fun errorState(error: String) {
		//error message
		Log.i("Scheduleeeeeeeeeeeeeee", error)
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
		} else {

		}

	}

	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			Log.i("USER", "aaaaaaaaaaaaa"+user.name)
			userViewModel.getUsers(user.userID)
			initUI()
		}
		Log.i("USER", "eeeeeeeeeeeeee")
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