package com.nlstechsolutions.libraries.image_picker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.BuildConfig
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.nlstechsolutions.libraries.image_picker.databinding.ImagePickerDialogBinding
import java.io.File
import java.io.IOException
import java.util.*

/**
 * mobile-app
 * @author Victor Oyando
 * @email oyandovic@gmail.com
 * Created 6/7/22 at 9:06 AM
 */
class ImagePicker(
    private val mType: CropType,
    private val callback: (uri: Uri, image: File) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: ImagePickerDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var currentImagePath: String
    private lateinit var uri: Uri

    internal enum class PickFrom {
        CAMERA, GALLERY
    }

    enum class CropType {
        SQUARE, FREE, RECTANGLE_HORIZONTAL, RECTANGLE_VERTICAL
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImagePickerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.camera.setOnClickListener {
            launchImagePicker(PickFrom.CAMERA)
        }

        binding.gallery.setOnClickListener {
            launchImagePicker(PickFrom.GALLERY)
        }

    }

    private fun launchImagePicker(from: PickFrom) {

        when (from) {
            PickFrom.CAMERA -> {
                if (!hasCameraPermission())
                    prepareCamera()
                else
                    takePictureFromCamera()
            }
            PickFrom.GALLERY -> {
                    takePictureFromGallery()
            }
        }

    }

    /**
     * CROP IMAGE
     */
    private val cropPictureLauncher = registerForActivityResult(CropImageContract()) { result ->
        when (result.isSuccessful) {
            false -> {
                Snackbar.make(binding.root, "Failed Cropping Image", Snackbar.LENGTH_SHORT)
                    .show()
                this.dismiss()
            }
            true -> {
                val uriContent = result.uriContent
                val uriFile = result.getUriFilePath(requireContext())?.let { File(it) }

                if (uriContent == null || uriFile == null) {
                    Snackbar.make(binding.root, "Failed Cropping Image", Snackbar.LENGTH_SHORT)
                        .show()
                    this.dismiss()
                    return@registerForActivityResult
                }

                this.dismiss()
                callback.invoke(uriContent, uriFile)

            }
        }

    }

    /**
     * LAUNCH CAMERA
     */
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                uri.cropImage()
            }
        }

    /**
     * LAUNCH GALLERY
     */
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.cropImage()
        }

    /**
     *  PERMISSION CONFIRMATION DIALOG
     */
    private fun showConfirmDialog(title: String, content: String, requestPermission: () -> Unit) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(content)
            .setNegativeButton("deny") { _, _ -> }
            .setPositiveButton("grant") { _, _ -> requestPermission.invoke() }
            .show()

    }

    /**
     * CAMERA PERMISSION
     */
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePictureFromCamera()
            } else {
                prepareCamera()
            }
        }

    /**
     * PREPARE CAMERA
     */
    private fun prepareCamera() {

        val cameraPermission =
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

        when (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            true -> takePictureFromCamera()
            false -> {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    requestCameraPermission(false)
                    StoreManager.rationaleShown(requireContext(), Manifest.permission.CAMERA)
                } else {

                    val hadDenied = StoreManager.rationalesList(requireContext())
                        .contains(Manifest.permission.CAMERA)
                    requestCameraPermission(hadDenied)

                }

            }
        }

    }

    /**
     * TAKE PICTURE
     */
    private fun takePictureFromCamera() {

        val file: File? = try {
            createImageFile()
        } catch (exception: IOException) {
            exception.printStackTrace()
            null
        }

        file?.also {
            uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName,
                it
            )

            cameraLauncher.launch(uri)
        }

    }

    /**
     * TAKE IMAGE FROM GALLERY
     */
    private fun takePictureFromGallery() {
        galleryLauncher.launch("image/*")
    }

    /**
     * REQUEST CAMERA PERMISSION
     */
    private fun requestCameraPermission(hadCompletelyDenied: Boolean) {

        val activity = requireActivity()

        if (hadCompletelyDenied)
            showConfirmDialog(
                title = "Camera Permission",
                content = getString(R.string.camera_permission_rationale)
            ) {
                openPermissionInSettings(activity)
            }
        else
            cameraPermissionLauncher.launch((Manifest.permission.CAMERA))

        dismiss()

    }

    /**
     * CHECK CAMERA PERMISSION
     */
    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    /**
     * NAVIGATE TO APP SETTINGS
     */
    private fun openPermissionInSettings(activity: FragmentActivity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.applicationContext.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }

    /**
     * CREATE IMAGE TEMPORAL FILE
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {

        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "_${Date().time}_",
            ".jpg",
            storageDir
        ).apply {
            currentImagePath = absolutePath
        }
    }

    private fun Uri.cropImage() {

        cropPictureLauncher.launch(
            options(uri = this) {
                setActivityMenuIconColor(Color.WHITE)
                setActivityTitle("Crop")
                setCropMenuCropButtonTitle("SAVE")
                setCropMenuCropButtonIcon(R.drawable.ic_baseline_done_24)
                setAllowRotation(true)
                setAllowCounterRotation(true)
                setAllowFlipping(true)
                setShowCropOverlay(true)
                setScaleType(CropImageView.ScaleType.CENTER)

                when (mType) {
                    CropType.SQUARE -> {
                        setAspectRatio(1, 1)
                        setCropShape(CropImageView.CropShape.OVAL)
                        setFixAspectRatio(true)
                    }
                    else -> {
                        setCropShape(CropImageView.CropShape.RECTANGLE)
                    }
                }

                setGuidelines(CropImageView.Guidelines.ON)
            }
        )

    }

}