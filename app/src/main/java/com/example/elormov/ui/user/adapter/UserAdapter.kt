package com.example.elormov.ui.user.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.domain.model.Student

class UserAdapter
	(private var studentsList: List<Student> = emptyList(),
	 private val onItemSelected:(Student) -> Unit): RecyclerView.Adapter<UserViewHolder>() {

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): UserViewHolder {
		return UserViewHolder(
			LayoutInflater
				.from(parent.context)
				.inflate(R.layout.item_user, parent, false)
		)
	}

	@SuppressLint("NotifyDataSetChanged")
	fun updateList (listUpdate: List<Student>) {
		studentsList = listUpdate
		notifyDataSetChanged()
	}

	override fun onBindViewHolder(
		holder: UserViewHolder,
		position: Int
	) {
		holder.render()
	}

	override fun getItemCount(): Int {
		return studentsList.size
	}
}