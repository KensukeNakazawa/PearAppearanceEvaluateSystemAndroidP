package com.example.pearappearanceevaluatesystemandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import java.util.concurrent.ExecutorService

import android.media.Image
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.camera.view.PreviewView
import com.example.pearappearanceevaluatesystemandroid.config.GlobalConst
import com.example.pearappearanceevaluatesystemandroid.utils.ImageUtil
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import kotlin.collections.ArrayList


class NewEvaluatePear : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var capturedImageList: ArrayList<ByteArray> = ArrayList()

    private var viewFinder: PreviewView? = null
    private var alertBuilder: AlertDialog.Builder? = null

    private val mediaTypeJpg = "image/jpeg".toMediaType()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_new_evaluate_pear)

        viewFinder = findViewById(R.id.view_finder)
        val cameraCaptureButton: Button = findViewById(R.id.camera_capture_button)

        alertBuilder = AlertDialog.Builder(this)
        alertBuilder?.setTitle("撮影しました")?.setMessage("120度程度回転させて再度撮影してください。")
            ?.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            ?.create()


        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraCaptureButton.setOnClickListener {
            takePhoto()
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({

            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder?.surfaceProvider)
                }


            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Log.e("Use case binding failed", e.toString())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            imageCaptureCallback
        )
    }

    private val imageCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
        @SuppressLint("UnsafeOptInUsageError")
        override fun onCaptureSuccess(imageProxy: ImageProxy) {
            super.onCaptureSuccess(imageProxy)
            val image: Image? = imageProxy.image

            if (image != null) {
                val imageByteArray = ImageUtil().imageToByteArray(image)
                if (imageByteArray != null) {
                    capturedImageList.add(imageByteArray)
                }

                val capturedNum = capturedImageList.size
                val capturedNumText: TextView = findViewById(R.id.captured_num_text)
                capturedNumText.text = "$capturedNum/3"
                if (capturedNum == 3) {

                    val cameraCaptureButton: Button = findViewById(R.id.camera_capture_button)
                    val progressBar: ProgressBar = findViewById(R.id.new_evaluate_pear_progress_bar)
                    val progressText: TextView = findViewById(R.id.progress_new_evaluate_text)

                    cameraCaptureButton.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    progressText.visibility = View.VISIBLE

                    val client = OkHttpClient.Builder().build()
                    val request = buildRequestBody(capturedImageList)


                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            println("Request Failed, message : $e")
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val jsonData = JSONObject(response.body?.string())
                            val pearId: Int = jsonData["pear_id"] as Int
                            val viewPastPearIntent =
                                Intent(applicationContext, ViewPastPearActivity::class.java)
                            viewPastPearIntent.putExtra("pearId", pearId)
                            startActivity(viewPastPearIntent)
                        }
                    })
                } else {
                    alertBuilder?.show()
                }
            }
            imageProxy.close()
            val msg = "Photo capture succeeded"
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildRequestBody(imageList: ArrayList<ByteArray>): Request {
        val requestURL = GlobalConst().defaultRequestUrl + "/pear/evaluates"

        // マルチパートで画像を送信するためのリクエストボディを作成
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (i in 0 until imageList.size) {
            requestBody.addFormDataPart(
                "pear_images",
                "side_of_pear_$i.png",
                imageList[i].toRequestBody(
                    mediaTypeJpg,
                    0,
                    imageList[i].size
                )
            )
        }

        val comRequestBody = requestBody.build()

        return Request.Builder()
            .url(requestURL)
            .post(comRequestBody)
            .build()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}