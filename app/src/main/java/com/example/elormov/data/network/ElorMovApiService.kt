package com.example.elormov.data.network

import com.example.elormov.domain.model.LoginRequest
import com.example.elormov.domain.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ElorMovApiService {
	@POST("api/login")

	suspend fun getAuth(@Body request: LoginRequest): Response<LoginResponse>
}
