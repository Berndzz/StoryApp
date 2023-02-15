package com.hardus.storyapp.authentication.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.authentication.login.LoginActivity
import com.hardus.storyapp.custom.Custom_Button
import com.hardus.storyapp.custom.Custom_EmailEditText
import com.hardus.storyapp.custom.Custom_PasswordEditText
import com.hardus.storyapp.databinding.ActivitySignupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class SignupActivity : AppCompatActivity() {

    private lateinit var signupBinding: ActivitySignupBinding
    private var signUpJob: Job = Job()
    private val signupViewModel: SignupViewModel by viewModels()

    private lateinit var myCustomButton: Custom_Button
    private lateinit var myCustomEditTextEmail: Custom_EmailEditText
    private lateinit var myCustomEditTextPassword: Custom_PasswordEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)


        myCustomButton = signupBinding.signupButton
        myCustomEditTextEmail = signupBinding.emailEditText
        myCustomEditTextPassword = signupBinding.passwordEditText

        setupView()
        setupAction()
        setMyCustomButtonEnable()
        textChanged()
        formAnimation()
    }

    private fun textChanged() {

        // Name
        signupBinding.nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyCustomButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Email
        myCustomEditTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyCustomButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        // Password
        myCustomEditTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyCustomButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    private fun setMyCustomButtonEnable() {
        val resultName = signupBinding.nameEditText.text
        myCustomButton.isEnabled = resultName != null && resultName.toString().isNotEmpty()
        val resultEmail = myCustomEditTextEmail.text
        myCustomButton.isEnabled = resultEmail != null && resultEmail.toString().isNotEmpty()
        val resultPassword = myCustomEditTextPassword.text
        myCustomButton.isEnabled = resultPassword != null && resultPassword.toString().isNotEmpty()
    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        supportActionBar?.hide()
    }

    private fun setupAction() {
        signupBinding.apply {
            myCustomButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = myCustomEditTextEmail.text.toString()
                val password = myCustomEditTextPassword.text.toString()
                showLoading(true)

                lifecycleScope.launchWhenResumed {
                    if (signUpJob.isActive) signUpJob.cancel()

                    signUpJob = launch {
                        signupViewModel.signUpUser(name, email, password).collect { result ->
                            result.onSuccess {
                                Toast.makeText(this@SignupActivity, "Success", Toast.LENGTH_SHORT)
                                    .show()
                                startActivity(
                                    Intent(
                                        this@SignupActivity,
                                        LoginActivity::class.java
                                    )
                                )
                            }
                            result.onFailure {
                                Toast.makeText(this@SignupActivity, "Error", Toast.LENGTH_SHORT)
                                    .show()
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun formAnimation() {
        ObjectAnimator.ofFloat(signupBinding.ivSignUp, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(signupBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView =
            ObjectAnimator.ofFloat(signupBinding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(signupBinding.nameEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(signupBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(signupBinding.emailEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(signupBinding.passwordTextView, View.ALPHA, 1f)
                .setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(signupBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val signup =
            ObjectAnimator.ofFloat(signupBinding.signupButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()

    }

    private fun showLoading(isLoading: Boolean) {
        signupBinding.apply {
            nameEditText.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
            signupButton.isEnabled = !isLoading

            if (isLoading) {
                signupBinding.progressbarSignUp.visibility = View.VISIBLE
            } else {
                signupBinding.progressbarSignUp.visibility = View.GONE
            }
        }
    }

}