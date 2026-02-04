package com.example.elormov.ui.schedule

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.elormov.databinding.FragmentScheduleBinding
import com.example.elormov.domain.model.Schedule
import com.example.elormov.domain.model.Modulo
import com.example.elormov.domain.model.Profesor
import com.example.elormov.domain.model.ScheduleResponse
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment : Fragment() {

	private var _binding: FragmentScheduleBinding? = null
	private val binding get() = _binding!!
	private lateinit var user: User
	private lateinit var sharedViewModel: SharedViewModel
	private val scheduleViewModel: ScheduleViewModel by viewModels()
	private lateinit var tableMakerFuns: TableMakerFuns

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		tableMakerFuns = TableMakerFuns(binding)
		initUser()
		initUIState()
	}

	private fun initUIState() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				scheduleViewModel.state.collect { state ->
					when (state) {
						is ScheduleState.Error -> errorState(state.error)
						ScheduleState.Loading -> loadingState()
						is ScheduleState.Success -> successState(state.schedule)
					}
				}
			}
		}
	}

	private fun successState(schedule: List<ScheduleResponse>) {
		binding.pb.visibility = View.GONE
		binding.horizontalScroll.visibility = View.VISIBLE
		binding.tvError.visibility = View.GONE

		val scheduleTable = convertSchedule(schedule)
		tableMakerFuns.createScheduleTable(scheduleTable)
	}

	private fun convertSchedule(schedule: List<ScheduleResponse>): List<Schedule> {
		val scheduleList = mutableListOf<Schedule>()
		for (item in schedule) {
			val schedule = Schedule(
				day = item.day,
				hour = item.hour,
				teacher = Profesor(
					name = item.teavherName.name
				),
				module = Modulo(
					name = item.module.name,
					nameEus = item.module.nameEus
				),
				classRoom = item.classroom ?: ""
			)
			scheduleList.add(schedule)
		}
		return scheduleList
	}

	private fun loadingState() {
		//dibujar loading
		binding.pb.visibility = View.VISIBLE
		binding.horizontalScroll.visibility = View.GONE
		binding.tvError.visibility = View.GONE
	}

	private fun errorState(error: String) {
		binding.pb.visibility = View.GONE
		binding.horizontalScroll.visibility = View.GONE
		binding.tvError.visibility = View.VISIBLE
		//error message
		Log.i("Scheduleeeeeeeeeeeeeee", error)
	}

	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			Log.i("USER", "aaaaaaaaaaaaa"+user.name)
			scheduleViewModel.getSchedule(user.type.name, user.userID)
		}
	}


	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentScheduleBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

}