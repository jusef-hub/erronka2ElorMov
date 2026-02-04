package com.example.elormov.ui.meeting

import com.example.elormov.domain.model.MeetingResponse

sealed class MeetingState {
	object Loading : MeetingState()
	class Error(val error: String) : MeetingState()
	class Success(val meetings: List<MeetingResponse>) : MeetingState()
}