package com.hardus.storyapp.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.hardus.storyapp.authentication.WelcomeActivity
import com.hardus.storyapp.databinding.ActivitySplashScreenBinding
import com.hardus.storyapp.main.MainActivity
import com.hardus.storyapp.util.Constant.EXTRA_TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class SplashScreen : AppCompatActivity() {

    private lateinit var splashScreenBinding: ActivitySplashScreenBinding
    private var token: String = ""
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(splashScreenBinding.root)

        supportActionBar?.hide()

        lifecycleScope.launchWhenCreated {
            launch {
                splashViewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        splashInfo()
    }

    private fun splashInfo() {
        lifecycleScope.launchWhenCreated {
            launch {
                splashViewModel.getAuthToken().collect { token ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (token.isNullOrEmpty()) {
                            Intent(this@SplashScreen, WelcomeActivity::class.java).also { intent ->
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Intent(this@SplashScreen, MainActivity::class.java).also { intent ->
                                intent.putExtra(EXTRA_TOKEN, token)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }, 1000)
                }
            }
        }
    }


}
