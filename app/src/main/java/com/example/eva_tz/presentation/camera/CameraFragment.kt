package com.example.eva_tz.presentation.camera

import android.content.ContentValues
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import com.example.eva_tz.R
import com.example.eva_tz.databinding.FragmentCameraBinding
import com.example.eva_tz.utils.base.BaseFragment
import com.google.common.util.concurrent.ListenableFuture
import org.koin.androidx.viewmodel.ext.android.viewModel

class CameraFragment : BaseFragment<FragmentCameraBinding, CameraViewModel>() {

    override val viewModel: CameraViewModel by viewModel()

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var zoomRatio: Float = 1.0f
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentCameraBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding.run {
            captureButton.setOnClickListener { takePhoto() }
            switchCameraButton.setOnClickListener { switchCamera() }
            clGalleryContainer.setOnClickListener {
                findNavController().navigate(R.id.galleryFragment)
            }
            previewView.setOnTouchListener { view, motionEvent ->
                scaleGestureDetector.onTouchEvent(motionEvent)
                true
            }
        }

        scaleGestureDetector = ScaleGestureDetector(
            requireContext(),
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val scaleFactor = detector.scaleFactor
                    zoomRatio = (zoomRatio * scaleFactor).coerceIn(
                        1.0f,
                        10.0f
                    )
                    updateZoomRatio(zoomRatio)
                    return true
                }

            })

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            { startCamera() },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    private fun updateZoomRatio(newZoomRatio: Float) {
        val cameraControl = camera?.cameraControl
        cameraControl?.setZoomRatio(newZoomRatio)
    }


    private fun startCamera() {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
            .also { it.surfaceProvider = binding.previewView.surfaceProvider }
        imageCapture = ImageCapture.Builder().build()

        try {
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        } catch (exc: Exception) {
            Log.e("CameraFragment", "Use case binding failed", exc)
        }
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            CameraSelector.DEFAULT_FRONT_CAMERA
        else
            CameraSelector.DEFAULT_BACK_CAMERA
        startCamera()
    }


    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        playCustomRingtone(R.raw.camera_shutter)
        animateFlash()
        val folderName = "evaTZ"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/$folderName"
            )
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            requireContext().contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    Log.d("CameraFragment", "Photo saved: $savedUri")
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }

    private fun playCustomRingtone(@RawRes customRingtoneResource: Int) {
        val customRingtoneUri =
            Uri.parse("android.resource://${context?.packageName}/$customRingtoneResource")
        val customRingtone = RingtoneManager.getRingtone(requireContext(), customRingtoneUri)

        customRingtone?.let {
            if (!it.isPlaying) {
                it.play()
            }
        }
    }

    private fun animateFlash() = binding.run {
        flashView.isVisible = true
        flashView.alpha = 1f

        flashView.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                flashView.isGone = true
            }
    }

    override fun initInsets() {
        observeNullable(viewModel.statusBarHeight) {
            if (it != null) { // sets android status bar height
                binding.statusBar.updateLayoutParams { height = it }
            }
        }
        observeNullable(viewModel.navigationBottomHeight) {
            if (it != null) { // sets android bottom navigation buttons height
                binding.navigationBar.updateLayoutParams { height = it }
            }
        }
    }
}