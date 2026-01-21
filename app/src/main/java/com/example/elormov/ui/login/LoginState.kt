package com.example.elormov.ui.login

import com.example.elormov.domain.model.LoginResponse

sealed class LoginState {
	object Loading: LoginState()
	class Error(val error: String): LoginState()
	class Success(val success: LoginResponse): LoginState()
}