package com.example.elormov.data.network

import com.example.elormov.domain.model.LoginRequest
import com.example.elormov.domain.model.CyclesResponse
import com.example.elormov.domain.model.LoginResponse
import com.example.elormov.domain.model.ScheduleResponse
import com.example.elormov.domain.model.TeacherResponse
import com.example.elormov.domain.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ElorMovApiService {
	// recoge los datos del usuario enviando el usuario y la contraseña
	// desde la consulta http://10.5.104.111:8080/api/login
	@POST("api/login")

	suspend fun getAuth(@Body request: LoginRequest): Response<LoginResponse>

	// recoge los horarios atraves del tipo de usuario y su id
	// desde de la consulta http://10.5.104.111:8080/api/horarios/{type}/{id}
	@GET("api/horarios/{type}/{id}")

	suspend fun getSchedule(
		@Path("type") type: String,
		@Path("id") id: Int
	): Response<List<ScheduleResponse>>

	// recoge los alumnos de un profesor atraves del id del profesor
	// desde la consulta http://10.5.104.111:8080/api/matriculaciones/profesor-alumnos/{id}
	@GET("api/matriculaciones/profesor-alumnos/{id}")

	suspend fun getAlums(@Path ("id") id: Int): Response<List<UserResponse>>

	// recoge los todos los ciclos
	// con la consulta http://10.5.104.111:8080/ciclos
	@GET("ciclos")

	suspend fun getCycles(): Response<List<CyclesResponse>>


	// recoge todos los profesores
	// desde la consulta http://10.5.104.111:8080/api/horarios/{type}/{id}
	@GET("api/profesores")

	suspend fun getTeachers(): Response<List<TeacherResponse>>
}
