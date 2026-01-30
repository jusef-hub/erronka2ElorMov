package com.example.elormov.ui.user.viewmodels

import com.example.elormov.domain.model.UserResponse

sealed class AlumState {
	object Loading : AlumState()
	class Error(val error: String) : AlumState()
	class Success(val alums: List<UserResponse>) : AlumState()
}