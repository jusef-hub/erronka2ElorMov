package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

class TeacherResponse (
	@SerializedName("id") val id: Int,
	@SerializedName("nombre") val name: String,
	@SerializedName("apellido") val lastName: String,
	@SerializedName("mail") val mail: String,
	@SerializedName("url") val image: String,
	@SerializedName("tipo") val type: TipoUsuario,

)