package com.example.elormov.ui.schedule

import com.example.elormov.domain.model.ScheduleResponse

sealed class ScheduleState {
	object Loading : ScheduleState()
	class Error(val error: String) : ScheduleState()
	class Success(val schedule: List<ScheduleResponse>) : ScheduleState()
}