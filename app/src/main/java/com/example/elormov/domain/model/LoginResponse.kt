package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

data class LoginResponse (
	@SerializedName("id") val userID: Int,
	@SerializedName("email") val mail: String,
	@SerializedName("nombre") val name: String,
	@SerializedName("apellidos") val lastName: String,
	@SerializedName("dni") val dni: String?,
	@SerializedName("direccion") val direccion: String?,
	@SerializedName("telefono1") val telefono1: String?,
	@SerializedName("argazkia_url") val argazkiaUrl: String?,
	@SerializedName("tipo") val tipo: TipoUsuario,
	@SerializedName("ciclo") val cycle: Cycle?,
	@SerializedName("semestre") val semester: Int?,
)

data class TipoUsuario(
	@SerializedName("id") val id: Int,
	@SerializedName("name") val name: String
)

data class Cycle(
	@SerializedName("id") val id: Int,
	@SerializedName("name") val name: String
)