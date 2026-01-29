package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

class UserResponse (
	@SerializedName("alumno") val alumno: Alum,
	@SerializedName("ciclo") val ciclo: Cycle,
	@SerializedName("curso") val curso: Int
)

data class Alum(
	@SerializedName("id") val userID: Int,
	@SerializedName("email") val mail: String,
	@SerializedName("nombre") val name: String,
	@SerializedName("apellidos") val lastName: String,
	@SerializedName("dni") val dni: String?,
	@SerializedName("direccion") val direccion: String?,
	@SerializedName("telefono1") val telefono1: String?,
	@SerializedName("argazkia_url") val argazkiaUrl: String?,
	@SerializedName("tipo") val tipo: TipoUsuario,
)

data class Cycle(
	@SerializedName("id") val id: Int,
	@SerializedName("nombre") val name: String
)