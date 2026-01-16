package com.example.elormov.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.elormov.R
import com.example.elormov.databinding.ActivityMainBinding
import com.example.elormov.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}
		//Deja la imagen del logo a la escala necesaria al iniciar la animación
		binding.ivLogo.scaleY = 0.1f
		binding.ivLogo.scaleX = 0.1f

		initUI()
		//Espera 0,5 segundos para iniciar la animacion
		binding.ivLogo.postDelayed({
			startAnimation()
		}, 500)
	}
	private fun initUI() {
		binding.btnLogin.setOnClickListener {
			val intent = Intent(this, HomeActivity::class.java)
			startActivity(intent)
			finish()
		}
	}

	private fun startAnimation() {
		// Rotación
		val rotation = ObjectAnimator.ofFloat(binding.ivLogo, View.ROTATION, 0f, 360f)
		rotation.duration = 2000

		// Escalar (hacer más grande y pequeño)
		val scaleX = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_X, 0.1f, 1f)
		val scaleY = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_Y, 0.1f, 1f)
		scaleX.duration = 2000
		scaleY.duration = 2000

		val animatorSet = AnimatorSet()
		animatorSet.playTogether(rotation, scaleX, scaleY)
		animatorSet.start()
	}
}