package com.example.elormov.ui.meeting

import android.annotation.SuppressLint
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
import com.example.elormov.R
import com.example.elormov.databinding.FragmentMeetingBinding
import com.example.elormov.databinding.FragmentScheduleBinding
import com.example.elormov.domain.model.Horario
import com.example.elormov.domain.model.Modulo
import com.example.elormov.domain.model.Profesor
import com.example.elormov.domain.model.ScheduleResponse
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.schedule.ScheduleState
import com.example.elormov.ui.schedule.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class MeetingFragment : Fragment() {

	private var _binding: FragmentMeetingBinding? = null
	private val binding get() = _binding!!
	private lateinit var user: User
	private lateinit var sharedViewModel: SharedViewModel
	private val meetingViewModel: ScheduleViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		initUser()
		initUIStateSchedule()
	}

	private fun initUIStateMeeting() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				meetingViewModel.state.collect { state ->
					when (state) {
						is ScheduleState.Error -> errorState(state.error)
						ScheduleState.Loading -> loadingState()
						is ScheduleState.Success -> successStateSchedule(state.schedule)
					}
				}
			}
		}
	}

	private fun initUIStateSchedule() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				meetingViewModel.state.collect { state ->
					when (state) {
						is ScheduleState.Error -> errorState(state.error)
						ScheduleState.Loading -> loadingState()
						is ScheduleState.Success -> successStateSchedule(state.schedule)
					}
				}
			}
		}
	}

	private fun successStateSchedule(schedule: List<ScheduleResponse>) {
		binding.pb.visibility = View.GONE
		binding.horizontalScroll.visibility = View.VISIBLE
		binding.tvError.visibility = View.GONE

		val scheduleTable = convertSchedule(schedule)
		createScheduleTable(scheduleTable)
	}

	private fun convertSchedule(schedule: List<ScheduleResponse>): List<Horario> {
		val scheduleList = mutableListOf<Horario>()
		for (item in schedule) {
			val horario = Horario(
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
			scheduleList.add(horario)
		}
		return scheduleList
	}

	/* Loading... */
	private fun loadingState() {
		//dibujar loading
		binding.pb.visibility = View.VISIBLE
		binding.horizontalScroll.visibility = View.GONE
		binding.tvError.visibility = View.GONE
	}

	/* ERROR */
	private fun errorState(error: String) {
		binding.horizontalScroll.visibility = View.GONE
		binding.pb.visibility = View.GONE
		binding.tvError.visibility = View.VISIBLE
		//error message
		Log.i("Scheduleeeeeeeeeeeeeee", error)
	}


	private fun initUser() {
		sharedViewModel.user.observe(viewLifecycleOwner) { user ->
			this.user = user
			Log.i("USER", "aaaaaaaaaaaaa"+user.name)
			meetingViewModel.getSchedule(user.type.name, user.userID)
		}
	}

	fun createScheduleTable(horarios: List<Horario>) {
		Log.i("Success","Success table")
		for (horario in horarios) {
			when (horario.day) {
				"LUNES" -> {
					initMonday(horario)
				}
				"MARTES" -> {
					initTuesday(horario)
				}
				"MIERCOLES" -> {
					initWednesday(horario)
				}
				"JUEVES" -> {
					initThursday(horario)
				}
				"VIERNES" -> {
					initFriday(horario)
				}
			}
		}
	}

	@SuppressLint("SetTextI18n")
	private fun initMonday(horario: Horario) {
		when (horario.hour) {
			1 -> {
				binding.cellMonday1.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
				Log.i("Schedule horario", horario.classRoom)
			}
			2 -> {
				binding.cellMonday2.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			3 -> {
				binding.cellMonday3.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			4 -> {
				binding.cellMonday4.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			5 -> {
				binding.cellMonday5.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			6 -> {
				binding.cellMonday6.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initTuesday(horario: Horario) {
		when (horario.hour) {
			1 -> {
				binding.cellTuesday1.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			2 -> {
				binding.cellTuesday2.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			3 -> {
				binding.cellTuesday3.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			4 -> {
				binding.cellTuesday4.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			5 -> {
				binding.cellTuesday5.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			6 -> {
				binding.cellTuesday6.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initWednesday(horario: Horario) {
		when (horario.hour) {
			1 -> {
				binding.cellWednesday1.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			2 -> {
				binding.cellWednesday2.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			3 -> {
				binding.cellWednesday3.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			4 -> {
				binding.cellWednesday4.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			5 -> {
				binding.cellWednesday5.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			6 -> {
				binding.cellWednesday6.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initThursday(horario: Horario) {
		when (horario.hour) {
			1 -> {
				binding.cellThursday1.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			2 -> {
				binding.cellThursday2.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			3 -> {
				binding.cellThursday3.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			4 -> {
				binding.cellThursday4.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			5 -> {
				binding.cellThursday5.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			6 -> {
				binding.cellThursday6.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initFriday(horario: Horario) {
		when (horario.hour) {
			1 -> {
				binding.cellFriday1.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			2 -> {
				binding.cellFriday2.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			3 -> {
				binding.cellFriday3.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			4 -> {
				binding.cellFriday4.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			5 -> {
				binding.cellFriday5.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
			6 -> {
				binding.cellFriday6.text = "${horario.module.name}\n${horario.teacher.name}\n${horario.classRoom}"
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentMeetingBinding.inflate(layoutInflater, container, false)
		return binding.root
	}
}
