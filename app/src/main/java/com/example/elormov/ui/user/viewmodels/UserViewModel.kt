package com.example.elormov.ui.user.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elormov.data.network.ElorMovApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val apiService: ElorMovApiService) : ViewModel() {
	private val _state = MutableStateFlow<UserState>(UserState.Loading)
	val state: StateFlow<UserState> = _state

	fun getUsers(id: Int) {
		viewModelScope.launch {
			_state.value = UserState.Loading
			val result = withContext(Dispatchers.IO) {
				apiService.getAlums(id)
			}
			if (result.body() != null) {
				_state.value = UserState.Success(result.body()!!)
			} else {
				val error = result.errorBody()?.string() ?: "Error desconocido"
				_state.value = UserState.Error(error)
			}
		}
	}
}