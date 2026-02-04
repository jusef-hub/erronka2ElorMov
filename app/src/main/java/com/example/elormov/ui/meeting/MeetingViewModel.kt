package com.example.elormov.ui.meeting

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
class MeetingViewModel @Inject constructor(private val apiService: ElorMovApiService): ViewModel() {
	private val _state = MutableStateFlow<MeetingState>(MeetingState.Loading)
	val state: StateFlow<MeetingState> = _state

	fun getMeetings(type: String,id: Int) {
		viewModelScope.launch {
			_state.value = MeetingState.Loading
			val result = withContext(Dispatchers.IO) {
				apiService.getMeetings(type, id)
			}
			if (result.body() != null) {
				_state.value = MeetingState.Success(result.body()!!)
			} else {
				val error = result.errorBody()?.string() ?: "Error desconocido"
				_state.value = MeetingState.Error(error)
			}
		}
	}
}