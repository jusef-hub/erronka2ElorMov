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
class TeacherViewModel @Inject constructor(private val apiService: ElorMovApiService): ViewModel() {
	private val _state = MutableStateFlow<TeacherState>(TeacherState.Loading)
	val state: StateFlow<TeacherState> = _state

	fun getTeachers() {
		viewModelScope.launch {
			_state.value = TeacherState.Loading
			val result = withContext(Dispatchers.IO) {
				apiService.getTeachers()
			}
			if (result.body() != null) {
				_state.value = TeacherState.Success(result.body()!!)
			} else {
				val error = result.errorBody()?.string() ?: "Error desconocido"
				_state.value = TeacherState.Error(error)
			}
		}
	}
}