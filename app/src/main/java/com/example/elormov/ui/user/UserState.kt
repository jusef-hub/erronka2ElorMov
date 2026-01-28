package com.example.elormov.ui.user

import com.example.elormov.domain.model.LoginResponse

sealed class UserState {
	object Loading : UserState()
	class Error(val error: String) : UserState()
	class Success(val users: List<LoginResponse>) : UserState()
}