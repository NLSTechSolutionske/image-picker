package com.nlstechsolutions.imagepicker

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.nlstechsolutions.imagepicker.databinding.ActivityMainBinding
import com.nlstechsolutions.libraries.image_picker.ImagePicker

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val uri = MutableLiveData<Uri?>(null)

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

        ImagePicker(ImagePicker.CropType.FREE) { uri, file ->
            Snackbar.make(binding.root, "Image Picked", Snackbar.LENGTH_SHORT).show()
            this.uri.value = uri
        }

    }

}