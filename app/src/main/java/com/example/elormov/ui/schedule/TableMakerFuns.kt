package com.example.elormov.ui.schedule

import android.annotation.SuppressLint
import android.util.Log
import com.example.elormov.databinding.FragmentScheduleBinding
import com.example.elormov.domain.model.Schedule

class TableMakerFuns (private val binding: FragmentScheduleBinding) {

	fun createScheduleTable(schedules: List<Schedule>) {
		Log.i("Success","Success table")
		for (schedule in schedules) {
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

}