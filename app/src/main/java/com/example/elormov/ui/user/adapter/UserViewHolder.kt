package com.example.elormov.ui.user.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.elormov.R
import com.example.elormov.databinding.ItemUserBinding
import com.example.elormov.domain.model.User

class UserViewHolder (view: View): RecyclerView.ViewHolder(view) {
	private val binding = ItemUserBinding.bind(view)
	@SuppressLint("SetTextI18n")
	fun render (user: User, onItemSelected: (User) -> Unit) {
		Glide.with(this.itemView.context)
			.load(user.argazkiaUrl)
			.placeholder(R.drawable.profile_placeholder)
			.error(R.drawable.profile_placeholder)
			.circleCrop()
			.into(binding.ivUser)

		binding.tvName.text = "${user.name} ${user.lastName}"
		if (user.type.id == 4) {
			binding.tvInfo.text = "${user.semester} ${user.cycle?.name}"
		}

		itemView.setOnClickListener { onItemSelected(user) }
	}
}