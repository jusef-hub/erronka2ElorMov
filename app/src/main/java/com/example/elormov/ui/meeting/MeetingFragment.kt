package com.example.elormov.ui.meeting

import android.annotation.SuppressLint
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
import com.example.elormov.databinding.FragmentMeetingBinding
import com.example.elormov.domain.model.Schedule
import com.example.elormov.domain.model.MeetingResponse
import com.example.elormov.domain.model.Modulo
import com.example.elormov.domain.model.Profesor
import com.example.elormov.domain.model.ScheduleResponse
import com.example.elormov.domain.model.User
import com.example.elormov.ui.home.SharedViewModel
import com.example.elormov.ui.schedule.ScheduleState
import com.example.elormov.ui.schedule.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class MeetingFragment : Fragment() {

	private var _binding: FragmentMeetingBinding? = null
	private val binding get() = _binding!!
	private lateinit var user: User
	private lateinit var sharedViewModel: SharedViewModel
	private val scheduleViewModel: ScheduleViewModel by viewModels()
	private val meetingViewModel: MeetingViewModel by viewModels()

	private var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
	private var meetingSuccess = false
	private var scheduleSuccess = false
	private var scheduleList = mutableListOf<Schedule>()
	private var meetingList = mutableListOf<Schedule>()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
		initUser()
		initUIStateSchedule()
		initUIStateMeeting()
	}

	/* --- Parte de Meeting --- */
	private fun initUIStateMeeting() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				meetingViewModel.state.collect { state ->
					when (state) {
						is MeetingState.Error -> errorState(state.error)
						MeetingState.Loading -> loadingState()
						is MeetingState.Success -> successStateMeeting(state.meetings)
					}
				}
			}
		}
	}

	private fun successStateMeeting(meetings: List<MeetingResponse>) {
		Log.i("Success Meeting","Success")
		meetingSuccess = true
		if (scheduleSuccess) {
			binding.pb.visibility = View.GONE
			binding.horizontalScroll.visibility = View.VISIBLE
			binding.tvError.visibility = View.GONE
		}

		convertMeeting(meetings)
		createScheduleTable()
	}

	private fun convertMeeting(meetings: List<MeetingResponse>) {

		for (item in meetings) {
			Log.i("bla bla bla",item.date)
			Log.i("bla bla bla",sdf.parse(item.date)?.day.toString())
			val day = when (sdf.parse(item.date)?.day) {
				1 -> "LUNES"
				2 -> "MARTES"
				3 -> "MIERCOLES"
				4 -> "JUEVES"
				5 -> "VIERNES"
				else -> Log.i("dia ERROR","ERROR")
			} as String
			Log.i("hora",sdf.parse(item.date)?.hours.toString())
			val time = when (sdf.parse(item.date)?.hours) {
				8 -> 1
				9 -> 2
				10 -> 3
				11 -> 4
				12 -> 5
				else -> Log.i("dia ERROR","ERROR")
			}
			val schedule = Schedule(
				day = day,
				hour = time,
				teacher = item.teacher,
				module = null,
				classRoom = item.classroom
			)
			meetingList.add(schedule)
		}
	}

	/* --- Parte de Schedule --- */
	private fun initUIStateSchedule() {
		viewLifecycleOwner.lifecycleScope.launch {
			viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
				scheduleViewModel.state.collect { state ->
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
		scheduleSuccess = true
		if (meetingSuccess) {
			binding.pb.visibility = View.GONE
			binding.horizontalScroll.visibility = View.VISIBLE
			binding.tvError.visibility = View.GONE
		}

		convertSchedule(schedule)
		createScheduleTable()
	}

	private fun convertSchedule(schedule: List<ScheduleResponse>) {

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
			scheduleViewModel.getSchedule(user.type.name, user.userID)
			meetingViewModel.getMeetings(user.type.name, user.userID)
		}
	}

	fun createScheduleTable() {
		Log.i("Success","Success table")
		for (schedule in scheduleList) {
			when (schedule.day) {
				"LUNES" -> {
					initMonday(schedule)
				}
				"MARTES" -> {
					initTuesday(schedule)
				}
				"MIERCOLES" -> {
					initWednesday(schedule)
				}
				"JUEVES" -> {
					initThursday(schedule)
				}
				"VIERNES" -> {
					initFriday(schedule)
				}
			}
		}
	}

	@SuppressLint("SetTextI18n")
	private fun initMonday(schedule: Schedule) {
		when (schedule.hour) {
			1 -> {
				binding.cellMonday1.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
				Log.i("Schedule schedule", schedule.classRoom.toString())
			}
			2 -> {
				binding.cellMonday2.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			3 -> {
				binding.cellMonday3.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			4 -> {
				binding.cellMonday4.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			5 -> {
				binding.cellMonday5.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			6 -> {
				binding.cellMonday6.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initTuesday(schedule: Schedule) {
		when (schedule.hour) {
			1 -> {
				binding.cellTuesday1.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			2 -> {
				binding.cellTuesday2.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			3 -> {
				binding.cellTuesday3.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			4 -> {
				binding.cellTuesday4.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			5 -> {
				binding.cellTuesday5.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			6 -> {
				binding.cellTuesday6.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initWednesday(schedule: Schedule) {
		when (schedule.hour) {
			1 -> {
				binding.cellWednesday1.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			2 -> {
				binding.cellWednesday2.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			3 -> {
				binding.cellWednesday3.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			4 -> {
				binding.cellWednesday4.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			5 -> {
				binding.cellWednesday5.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			6 -> {
				binding.cellWednesday6.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initThursday(schedule: Schedule) {
		when (schedule.hour) {
			1 -> {
				binding.cellThursday1.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			2 -> {
				binding.cellThursday2.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			3 -> {
				binding.cellThursday3.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			4 -> {
				binding.cellThursday4.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			5 -> {
				binding.cellThursday5.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			6 -> {
				binding.cellThursday6.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
		}
	}
	@SuppressLint("SetTextI18n")
	private fun initFriday(schedule: Schedule) {
		when (schedule.hour) {
			1 -> {
				binding.cellFriday1.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			2 -> {
				binding.cellFriday2.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			3 -> {
				binding.cellFriday3.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			4 -> {
				binding.cellFriday4.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			5 -> {
				binding.cellFriday5.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
			}
			6 -> {
				binding.cellFriday6.text = "${schedule.module?.name}\n${schedule.teacher.name}\n${schedule.classRoom}"
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
