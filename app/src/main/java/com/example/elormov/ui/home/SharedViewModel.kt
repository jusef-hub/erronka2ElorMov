package com.example.elormov.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.elormov.domain.model.User

class SharedViewModel : ViewModel() {
	val user = MutableLiveData<User>()
}