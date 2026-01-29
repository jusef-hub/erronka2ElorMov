package com.example.elormov.ui.user.viewmodels

import com.example.elormov.domain.model.UserResponse

sealed class UserState {
	object Loading : UserState()
	class Error(val error: String) : UserState()
	class Success(val users: List<UserResponse>) : UserState()
}