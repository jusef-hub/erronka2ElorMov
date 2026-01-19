package com.example.elormov.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.elormov.databinding.FragmentUserBinding
import com.example.elormov.ui.user.adapter.UserAdapter
import kotlinx.coroutines.launch

class UserFragment : Fragment() {

	private var _binding: FragmentUserBinding? = null
	private val binding get() = _binding!!
	private lateinit var studentTeacherAdapter: UserAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initUI()
	}

	private fun initUI() {
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {

			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentUserBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

}