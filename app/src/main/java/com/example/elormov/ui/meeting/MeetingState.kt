package com.example.elormov.ui.meeting

import com.example.elormov.domain.model.ScheduleResponse
import com.example.elormov.ui.schedule.ScheduleState

sealed class MeetingState {
	object Loading : MeetingState()
	class Error(val error: String) : MeetingState()
	class Success(val schedule: List<ScheduleResponse>) : MeetingState()
}