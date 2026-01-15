package com.example.elormov.ui.studteach

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.elormov.R
import com.example.elormov.databinding.FragmentScheduleBinding
import com.example.elormov.databinding.FragmentStudTeachBinding

class StudTeachFragment : Fragment() {

	private var _binding: FragmentStudTeachBinding? = null
	private val binding get() = _binding!!

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		_binding = FragmentStudTeachBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

}