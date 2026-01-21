package com.example.elormov.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elormov.data.network.ElorMovApiService
import com.example.elormov.domain.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val apiService: ElorMovApiService): ViewModel(){
	private val _state = MutableStateFlow<LoginState>(LoginState.Loading)
	val state: StateFlow<LoginState> = _state

	fun getAuth(username: String, password: String) {

		// Iniciamos la corrutina para peticiones de red
		viewModelScope.launch {
			_state.value = LoginState.Loading

			try {
				// Creamos el objeto Request con los datos reales del formulario
				val loginData = LoginRequest(username = username, password = password)

				// Ejecutamos la llamada en un hilo de E/S (IO)
				val response = withContext(Dispatchers.IO) {
					apiService.getAuth(loginData) // Asegúrate de que en la interfaz se llame login o getAuth
				}

				if (response.isSuccessful) {
					val user = response.body()
					if (user != null) {
						// Notificamos éxito a la UI
						_state.value = LoginState.Success(user)
						Log.i("LOGIN_OK", "Usuario logueado: ${user}")
					}
				} else {
					// Capturamos el error 401 o 403 del backend
					val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
					_state.value = LoginState.Error(errorMsg)
					Log.e("LOGIN_ERROR", errorMsg)
				}

			} catch (e: Exception) {
				// Error de conexión (Servidor apagado, falta de internet, etc.)
				_state.value = LoginState.Error("No se pudo conectar con el servidor")
				Log.e("LOGIN_EXCEPTION", e.message.toString())
			}
		}
	}

}