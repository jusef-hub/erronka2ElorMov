package com.example.elormov.ui.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.elormov.databinding.FragmentScheduleBinding
import com.example.elormov.domain.model.Schedule
import com.example.elormov.domain.model.Modulo
import com.example.elormov.domain.model.Profesor
import com.example.elormov.domain.model.ScheduleResponse
import com.example.elormov.ui.schedule.ScheduleState
import com.example.elormov.ui.schedule.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TeacherScheduleDialogFragment : DialogFragment() {

	private var _binding: FragmentScheduleBinding? = null
	private val binding get() = _binding!!
	private val scheduleViewModel: ScheduleViewModel by viewModels()
	private var teacherId: Int = -1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		teacherId = requireArguments().getInt("teacher_id", -1)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentScheduleBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		scheduleViewModel.getSchedule("profesor", teacherId)
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
		createScheduleTable(scheduleTable)
	}

	private fun convertSchedule(schedule: List<ScheduleResponse>): List<Schedule> {
		return schedule.map { item ->
			Schedule(
				day = item.day,
				hour = item.hour,
				teacher = Profesor(name = item.teavherName.name),
				module = Modulo(item.module.name, item.module.nameEus),
				classRoom = item.classroom ?: ""
			)
		}
	}

	private fun loadingState() { binding.pb.visibility = View.VISIBLE }
	private fun errorState(error: String) { binding.pb.visibility = View.GONE; Log.e("Schedule", error) }

	private fun createScheduleTable(horarios: List<Schedule>) {
		for (horario in horarios) {
			when (horario.day) {
				"LUNES" -> setCell(binding.cellMonday1, binding.cellMonday2, binding.cellMonday3, binding.cellMonday4, binding.cellMonday5, binding.cellMonday6, horario)
				"MARTES" -> setCell(binding.cellTuesday1, binding.cellTuesday2, binding.cellTuesday3, binding.cellTuesday4, binding.cellTuesday5, binding.cellTuesday6, horario)
				"MIERCOLES" -> setCell(binding.cellWednesday1, binding.cellWednesday2, binding.cellWednesday3, binding.cellWednesday4, binding.cellWednesday5, binding.cellWednesday6, horario)
				"JUEVES" -> setCell(binding.cellThursday1, binding.cellThursday2, binding.cellThursday3, binding.cellThursday4, binding.cellThursday5, binding.cellThursday6, horario)
				"VIERNES" -> setCell(binding.cellFriday1, binding.cellFriday2, binding.cellFriday3, binding.cellFriday4, binding.cellFriday5, binding.cellFriday6, horario)
			}
		}
	}

	private fun setCell(
		c1: View, c2: View, c3: View, c4: View, c5: View, c6: View,
		horario: Schedule
	) {
		val text = "${horario.module?.name}\n${horario.teacher.name}\n${horario.classRoom}"
		when(horario.hour) {
			1 -> (c1 as? android.widget.TextView)?.text = text
			2 -> (c2 as? android.widget.TextView)?.text = text
			3 -> (c3 as? android.widget.TextView)?.text = text
			4 -> (c4 as? android.widget.TextView)?.text = text
			5 -> (c5 as? android.widget.TextView)?.text = text
			6 -> (c6 as? android.widget.TextView)?.text = text
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	override fun onStart() {
		super.onStart()
		dialog?.window?.setLayout(
			(resources.displayMetrics.widthPixels * 0.9).toInt(),
			(resources.displayMetrics.heightPixels * 0.7).toInt()
		)
	}
}
