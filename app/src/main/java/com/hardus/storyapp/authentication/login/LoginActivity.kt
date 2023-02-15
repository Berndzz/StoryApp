package com.hardus.storyapp.authentication.login

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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.R
import com.hardus.storyapp.custom.Custom_Button
import com.hardus.storyapp.custom.Custom_EmailEditText
import com.hardus.storyapp.custom.Custom_PasswordEditText
import com.hardus.storyapp.databinding.ActivityLoginBinding
import com.hardus.storyapp.main.MainActivity
import com.hardus.storyapp.util.Constant.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private var loginJob: Job = Job()
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var myCustomButton: Custom_Button
    private lateinit var myCustomEditTextEmail: Custom_EmailEditText
    private lateinit var myCustomEditTextPassword: Custom_PasswordEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        myCustomButton = loginBinding.loginButton
        myCustomEditTextEmail = loginBinding.emailEditText
        myCustomEditTextPassword = loginBinding.passwordEditText

        setupView()
        setupAction()
        setMyCustomButtonEnable()
        textChanged()
        formAnimation()
    }

    private fun textChanged() {

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
        val resultEmail = myCustomEditTextEmail.text
        myCustomButton.isEnabled = resultEmail != null && resultEmail.toString().isNotEmpty()
        val resultPassword = myCustomEditTextPassword.text
        myCustomButton.isEnabled = resultPassword != null && resultPassword.toString().isNotEmpty()
    }


    private fun setupView() {
        window.insetsController?.hide(WindowInsets.Type.statusBars())
        supportActionBar?.hide()
    }

    private fun setupAction() {
        loginBinding.apply {
            myCustomButton.setOnClickListener {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                showLoading(true)

                lifecycleScope.launchWhenResumed {
                    if (loginJob.isActive) loginJob.cancel()

                    loginJob = launch {
                        loginViewModel.loginUser(email, password).collect { result ->
                            result.onSuccess { credentials ->
                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle("Hore...")
                                    setMessage(getString(R.string.alert_massage_login))
                                    setPositiveButton(getString(R.string.next)) { _, _ ->
                                        credentials.loginResult?.name?.let { name ->
                                            loginViewModel.saveNameUser(name)
                                        }
                                        credentials.loginResult?.token?.let { token ->
                                            loginViewModel.saveToken(token)
                                            Intent(
                                                this@LoginActivity,
                                                MainActivity::class.java
                                            ).also { intent ->
                                                intent.putExtra(EXTRA_TOKEN, token)
                                                startActivity(intent)
                                                finishAffinity()
                                            }
                                        }
                                    }
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Success",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    create()
                                    show()
                                }
                            }

                            result.onFailure {
                                Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_SHORT)
                                    .show()
                                showLoading(false)
                            }
                        }
                    }
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loginBinding.progressbarLogin.visibility = View.VISIBLE
        } else {
            loginBinding.progressbarLogin.visibility = View.GONE
        }
    }


    private fun formAnimation() {
        ObjectAnimator.ofFloat(loginBinding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(loginBinding.tvCaption1, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(loginBinding.tvCaption2, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(loginBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(loginBinding.emailEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(loginBinding.passwordTextView, View.ALPHA, 1f)
                .setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(loginBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val login =
            ObjectAnimator.ofFloat(loginBinding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 500
        }.start()
    }
}

