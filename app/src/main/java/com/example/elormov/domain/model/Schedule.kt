package com.example.elormov.domain.model


data class Schedule(
	val day: String,
	val hour: Int,
	val teacher: Profesor,
	val module: Modulo?,
	val classRoom: String?
)
