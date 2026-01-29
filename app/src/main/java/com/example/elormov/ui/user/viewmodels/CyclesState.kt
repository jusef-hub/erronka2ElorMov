package com.example.elormov.ui.user.viewmodels

import com.example.elormov.domain.model.CyclesResponse

sealed class CyclesState {
	object Loading : CyclesState()
	class Error(val error: String) : CyclesState()
	class Success(val users: List<CyclesResponse>) : CyclesState()
}