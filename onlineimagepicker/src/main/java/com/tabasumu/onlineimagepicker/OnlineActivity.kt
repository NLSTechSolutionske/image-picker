package com.tabasumu.onlineimagepicker

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.tabasumu.libraries.image_picker.ImagePicker
import com.tabasumu.onlineimagepicker.databinding.ActivityOnlineBinding

class OnlineActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnlineBinding

    private val uri = MutableLiveData<Uri?>(null)

    private val TAG = "OnlineActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineBinding.inflate(LayoutInflater.from(this))
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

        ImagePicker.Builder(this)
            .cropType(ImagePicker.CropType.FREE)
            .resultUri { uri, file ->

                Log.i(TAG, "pickImage: URI EXISTS -> ${uri != null}")
                Log.i(TAG, "pickImage: URI PATH -> ${uri.path}")

                Log.i(TAG, "pickImage: FILE EXISTS -> ${file.exists()}")
                Log.i(TAG, "pickImage: FILE PATH -> ${file.path}")

                try {
                    Log.i(TAG, "pickImage: URI TO FILE EXISTS -> ${uri.toFile().exists()}")
                    Log.i(TAG, "pickImage: URI FILE PATH -> ${uri.toFile().path}")
                } catch (e: Exception) {
                    Log.e(TAG, "pickImage: ${e.localizedMessage}")
                }

                this.uri.value = uri

            }.show()

    }


}