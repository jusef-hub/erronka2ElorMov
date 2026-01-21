package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

class User (
	userID: Int,
	mail: String,
	name: String,
	lastName: String,
	dni: String?,
	direccion: String?,
	telefono1: String?,
	argazkiaUrl: String?,
	tipo: TipoUsuario,
)