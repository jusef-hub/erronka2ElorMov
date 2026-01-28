package com.example.elormov.domain.model


class User (
	val userID: Int,
	val mail: String,
	val name: String,
	val lastName: String,
	val dni: String?,
	val direccion: String?,
	val telefono1: String?,
	val argazkiaUrl: String?,
	val type: TipoUsuario,
	val cycle: Cycle?,
	val semester: Int?,
)