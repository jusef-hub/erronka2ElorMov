package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

class MeetingResponse (
	@SerializedName("estado") val state: String,
	@SerializedName("titulo") val title: String?,
	@SerializedName("asunto") val affair: String?,
	@SerializedName("aula") val classroom: String?,
	@SerializedName("fecha") val date: String,
	@SerializedName("profesor") val teacher: Profesor,
	@SerializedName("alumno") val alum: Alum,

)