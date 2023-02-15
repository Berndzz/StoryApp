package com.hardus.storyapp.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.authentication.login.LoginActivity
import com.hardus.storyapp.authentication.signup.SignupActivity
import com.hardus.storyapp.databinding.ActivityWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalPagingApi
class WelcomeActivity : AppCompatActivity() {

    private lateinit var welcomeBinding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(welcomeBinding.root)

        setupView()
        setupAction()
        formAnimation()
    }


    private fun setupAction() {
        welcomeBinding.loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        welcomeBinding.signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        supportActionBar?.hide()
    }

    private fun formAnimation() {
        ObjectAnimator.ofFloat(welcomeBinding.ivWelcome, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login =
            ObjectAnimator.ofFloat(welcomeBinding.loginButton, View.ALPHA, 1f).setDuration(500)
        val signUp =
            ObjectAnimator.ofFloat(welcomeBinding.signupButton, View.ALPHA, 1f).setDuration(500)
        val captionOne =
            ObjectAnimator.ofFloat(welcomeBinding.tvCaption1, View.ALPHA, 1f).setDuration(500)
        val captionTwo =
            ObjectAnimator.ofFloat(welcomeBinding.tvCaption2, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(login, signUp)
        }

        AnimatorSet().apply {
            playSequentially(captionOne, captionTwo, together)
            start()
        }
    }
}