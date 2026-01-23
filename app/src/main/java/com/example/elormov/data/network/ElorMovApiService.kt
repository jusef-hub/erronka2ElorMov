package com.example.elormov.data.network

import com.example.elormov.domain.model.LoginRequest
import com.example.elormov.domain.model.LoginResponse
import com.example.elormov.domain.model.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ElorMovApiService {
	@POST("api/login")

	suspend fun getAuth(@Body request: LoginRequest): Response<LoginResponse>

	@GET("api/horarios/alumno/{id}")

	suspend fun getSchedule(@Path("id") id: Int): Response<ScheduleResponse>
}
