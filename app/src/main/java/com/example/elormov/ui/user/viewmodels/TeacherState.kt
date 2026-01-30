package com.example.elormov.ui.user.viewmodels

import com.example.elormov.domain.model.TeacherResponse

sealed class TeacherState {
	object Loading : TeacherState()
	class Error(val error: String) : TeacherState()
	class Success(val teachers: List<TeacherResponse>) : TeacherState()
}