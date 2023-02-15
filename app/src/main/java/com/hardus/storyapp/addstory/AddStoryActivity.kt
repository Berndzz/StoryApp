package com.hardus.storyapp.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.hardus.storyapp.R
import com.hardus.storyapp.camera.CameraActivity
import com.hardus.storyapp.databinding.ActivityAddStoryBinding
import com.hardus.storyapp.main.MainActivity
import com.hardus.storyapp.util.Constant.EXTRA_TOKEN
import com.hardus.storyapp.util.Constant.TAG
import com.hardus.storyapp.util.reduceFileSize
import com.hardus.storyapp.util.rotateBitmap
import com.hardus.storyapp.util.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
@ExperimentalPagingApi
class AddStoryActivity : AppCompatActivity() {

    private lateinit var addStoryBinding: ActivityAddStoryBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFileStory: File? = null
    private var location: Location? = null
    private var token: String = ""

    private val addStoryViewModel: AddStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)

        supportActionBar?.title = getString(R.string.new_story)
        addStoryBinding.switchLocation.text = getString(R.string.location_text)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        lifecycleScope.launchWhenCreated {
            launch {
                addStoryViewModel.getAuthToken().collect { authToken ->
                    if (!authToken.isNullOrEmpty()) token = authToken
                }
            }
        }

        allPermission()
        addStoryBinding.btnUploadCamera.setOnClickListener { startCamera() }
        addStoryBinding.btnUploadImage.setOnClickListener { startGallery() }
        addStoryBinding.uploadStory.setOnClickListener { startUpload() }
        addStoryBinding.switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLastLocation()
            } else {
                this.location = null
            }

        }
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                    Log.d(TAG, "getLastLocation: ${location.latitude}, ${location.longitude}")
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.enable_location),
                        Toast.LENGTH_SHORT
                    ).show()

                    addStoryBinding.switchLocation.isChecked = false
                }
            }
        } else {
            // Location permission denied
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->
        Log.d(TAG, "$permission")
        when {
            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getLastLocation()
            }
            else -> {
                Snackbar
                    .make(
                        addStoryBinding.root,
                        getString(R.string.alert_location_not_allowed),
                        Snackbar.LENGTH_SHORT
                    )
                    .setActionTextColor(getColor(R.color.blue_3))
                    .setAction(getString(R.string.alert_upload_story)) {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()
                addStoryBinding.switchLocation.isChecked = false
            }
        }
    }


    private fun allPermission() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }


    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startUpload() {
        var isValid = true

        addStoryBinding.apply {
            if (edtDescription.text.toString().isBlank()) {
                edtDescription.error = "Field Kosong"
                isValid = false
            }

            if (getFileStory == null) {
                Snackbar.make(
                    addStoryBinding.root,
                    "File kosong",
                    Snackbar.LENGTH_SHORT
                ).show()
                isValid = false
            }

            if (isValid) {
                lifecycleScope.launchWhenCreated {
                    launch {
                        val file = reduceFileSize(getFileStory as File)
                        val desc =
                            "${edtDescription.text}".toRequestBody("text/plain".toMediaType())
                        val imgFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo",
                            file.name,
                            imgFile
                        )

                        var lat: RequestBody? = null
                        var lon: RequestBody? = null

                        if (location != null) {
                            lat =
                                location?.latitude.toString()
                                    .toRequestBody("text/plain".toMediaType())
                            lon =
                                location?.longitude.toString()
                                    .toRequestBody("text/plain".toMediaType())
                        }

                        addStoryViewModel.uploadImage(token, imageMultipart, desc, lat, lon)
                            .collect { response ->
                                response.onSuccess {
                                    val intent =
                                        Intent(this@AddStoryActivity, MainActivity::class.java)
                                    intent.putExtra(EXTRA_TOKEN, token)
                                    startActivity(intent)
                                    Toast.makeText(
                                        this@AddStoryActivity,
                                        "Story terapload",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                                response.onFailure {
                                    Toast.makeText(
                                        this@AddStoryActivity,
                                        getString(R.string.alert_upload_story),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.alert_no_permision_location),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val files = uriToFile(selectedImg, this@AddStoryActivity)
            getFileStory = files
            addStoryBinding.ivPreview.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFileStory = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            addStoryBinding.ivPreview.setImageBitmap(result)
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 0
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}

