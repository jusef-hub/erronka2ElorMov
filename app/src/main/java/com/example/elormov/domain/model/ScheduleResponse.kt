package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

class ScheduleResponse (
	@SerializedName("dia") val day: String,
	@SerializedName("hora") val hour: Int,
	@SerializedName("modulo") val module: Modulo,
	@SerializedName("aula") val classroom: String?,
	@SerializedName("profesor") val teavherName: Profesor,

)
data class Profesor(
	@SerializedName("nombre") val name: String,
)
data class Modulo(
	@SerializedName("nombre") val name: String,
	@SerializedName("nombre_eus") val nameEus: String,
)