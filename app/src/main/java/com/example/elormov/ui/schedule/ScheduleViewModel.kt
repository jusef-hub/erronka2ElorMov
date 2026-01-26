package com.example.elormov.ui.schedule

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
class ScheduleViewModel @Inject constructor(private val apiService: ElorMovApiService) : ViewModel() {
	private val _state = MutableStateFlow<ScheduleState>(ScheduleState.Loading)
	val state: StateFlow<ScheduleState> = _state

	fun getSchedule(type: String,id: Int) {
		viewModelScope.launch {
			_state.value = ScheduleState.Loading
			val result = withContext(Dispatchers.IO) {
				apiService.getSchedule(type, id)
			}
			if (result.body() != null) {
				_state.value = ScheduleState.Success(result.body()!!)
			} else {
				val error = result.errorBody()?.string() ?: "Error desconocido"
				_state.value = ScheduleState.Error(error)
			}
		}
	}
}