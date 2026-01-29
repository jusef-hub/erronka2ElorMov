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
class CyclesViewModel @Inject constructor(private val apiService: ElorMovApiService) : ViewModel() {
	private val _state = MutableStateFlow<CyclesState>(CyclesState.Loading)
	val state: StateFlow<CyclesState> = _state

	fun getCycles() {
		viewModelScope.launch {
			_state.value = CyclesState.Loading
			val result = withContext(Dispatchers.IO) {
				apiService.getCiclos()
			}
			if (result.body() != null) {
				_state.value = CyclesState.Success(result.body()!!)
			} else {
				val error = result.errorBody()?.string() ?: "Error desconocido"
				_state.value = CyclesState.Error(error)
			}
		}
	}
}