package com.example.elormov.domain.model

import com.google.gson.annotations.SerializedName

class TeacherResponse (
	@SerializedName("id") val id: Int,
	@SerializedName("nombre") val name: String,
)