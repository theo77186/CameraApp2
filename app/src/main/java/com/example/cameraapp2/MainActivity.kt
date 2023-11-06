package com.example.cameraapp2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun captureOnClick(view: View) {
        // request perms
        ActivityCompat.requestPermissions(this@MainActivity, arrayOf("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"),0 )

        val controller = LifecycleCameraController(applicationContext)
        val imageCapture = ImageCapture.Builder().setTargetRotation(view.display.rotation).build()
        val filename: String = getExternalFilesDir(null).toString() + "" + System.currentTimeMillis() + ".jpg"
        val file = File(filename)
        val outputFileOptions = OutputFileOptions.Builder(file).build()
        val imageSavedCallback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.i("camera", "photo written on " + outputFileResults.savedUri.toString())
                Looper.prepare()
                Toast.makeText(this@MainActivity, "photo written on " + outputFileResults.savedUri.toString(), Toast.LENGTH_SHORT).show()
                //val intent = Intent().setAction(Intent.ACTION_VIEW).setData(outputFileResults.savedUri)
                //startActivity(intent)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(this@MainActivity, "error: " + exception.toString(), Toast.LENGTH_LONG).show()
            }
        }
        controller.bindToLifecycle(this)
        controller.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        val cameraProviderF = ProcessCameraProvider.getInstance(this)
        val cameraProvider = cameraProviderF.get()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(this, controller.cameraSelector, imageCapture)
        Log.i("camera", "before takepicture")
        imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(), imageSavedCallback)
    }
}
