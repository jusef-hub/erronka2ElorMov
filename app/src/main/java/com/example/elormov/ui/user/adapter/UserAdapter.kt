package com.example.elormov.ui.user.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elormov.R
import com.example.elormov.domain.model.User

class UserAdapter
	(private var studentsList: List<User> = emptyList(),
	 private val onItemSelected:(User) -> Unit): RecyclerView.Adapter<UserViewHolder>() {

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
	fun updateList (listUpdate: List<User>) {
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