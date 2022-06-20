package com.nlstechsolutions.imagepicker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.nlstechsolutions.imagepicker.databinding.ActivityMainBinding
import com.nlstechsolutions.libraries.image_picker.ImagePicker
import java.io.File

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val uri = MutableLiveData<Uri?>(null)

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        uri.observe(this) {
            binding.tvImage.isVisible = it == null
            if (it != null) binding.ivImage.setImageURI(it)
        }

        binding.btnPick.setOnClickListener {
            pickImage()
        }

    }

    private fun pickImage() {
        ImagePicker.Builder(this).cropType(ImagePicker.CropType.FREE).resultUri { uri, file ->

            Log.i(TAG, "pickImage: FILE EXISTS -> ${file.exists()}")
            Log.i(TAG, "pickImage: FILE URI EXISTS -> ${File(uri.path).exists()}")

            this.uri.value = uri
        }.show()

    }

}