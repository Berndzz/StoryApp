package com.hardus.storyapp.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hardus.storyapp.R
import com.hardus.storyapp.authentication.WelcomeActivity
import com.hardus.storyapp.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalPagingApi
class ProfileActivity : AppCompatActivity() {

    private lateinit var profileBinding: ActivityProfileBinding
    private var token: String = ""
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(profileBinding.root)


        lifecycleScope.launchWhenCreated {
            launch {
                profileViewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            launch {
                profileViewModel.getNameUser().collect { name ->
                    if (!name.isNullOrEmpty()) profileBinding.nameTextView.text =
                        getString(R.string.greeting, name)
                }
            }
        }


        setupView()
        setupAction()
    }


    private fun setupView() {
        supportActionBar?.title = getString(R.string.title_activity_profile)
    }

    private fun setupAction() {
        profileBinding.logoutButton.setOnClickListener {
            showLogout()
        }
    }

    private fun showLogout() {
        MaterialAlertDialogBuilder(this@ProfileActivity)
            .setTitle(getString(R.string.alert_logout))
            .setMessage(getString(R.string.alert_dialog_logout))
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { _, _ ->
                profileViewModel.saveAuthToken("")
                profileViewModel.saveNameUser("")
                Intent(this@ProfileActivity, WelcomeActivity::class.java).also { intent ->
                    startActivity(intent)
                    finishAffinity()
                }
                Toast.makeText(
                    this@ProfileActivity,
                    getString(R.string.alert_logout_successfully),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            .show()
    }

}